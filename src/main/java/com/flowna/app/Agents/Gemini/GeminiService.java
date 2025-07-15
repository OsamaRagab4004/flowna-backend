package com.flowna.app.Agents.Gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Retry retry;
    private final AsyncTaskExecutor taskExecutor; // Inject context-aware executor


    @Autowired
    public GeminiService(RetryRegistry retryRegistry, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.retry = retryRegistry.retry("geminiServiceRetry");
        this.taskExecutor = taskExecutor;
    }


    @Async
    public CompletableFuture<GeminiResponse> getGeminiResponse(GeminiRequest geminiRequest) {
        Function<GeminiRequest, CompletableFuture<GeminiResponse>> decoratedFunction =
                Retry.decorateFunction(retry, this::sendRequest);

        return decoratedFunction.apply(geminiRequest);
    }

    @Async
    protected CompletableFuture<GeminiResponse> sendRequest(GeminiRequest geminiRequest) {
        String requestBodyJson = "{"
                + "\"contents\": ["
                + "{"
                + "\"role\": \"user\","
                + "\"parts\": ["
                + "{"
                + "\"text\": \"" + escapeJson(geminiRequest.getUser_prompt()) + "\""
                + "}"
                + "]"
                + "}"
                + "],"
                + "\"systemInstruction\": {"
                + "\"role\": \"user\","
                + "\"parts\": ["
                + "{"
                + "\"text\": \"" + escapeJson(geminiRequest.getModel_prompt()) + "\""
                + "}"
                + "]"
                + "},"
                + "\"generationConfig\": {"
                + "\"temperature\": 1,"
                + "\"topK\": 40,"
                + "\"topP\": 0.95,"
                + "\"maxOutputTokens\": 8192,"
                + "\"responseMimeType\": \"text/plain\""
                + "}"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + geminiRequest.getModel_typ() + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenComposeAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode root = objectMapper.readTree(response.body());
                            JsonNode candidate = root.path("candidates").get(0);
                            String message = candidate.path("content").path("parts").get(0).path("text").asText();
                            int promptTokens = root.path("usageMetadata").path("promptTokenCount").asInt();
                            int responseTokens = root.path("usageMetadata").path("candidatesTokenCount").asInt();
                            int totalTokens = root.path("usageMetadata").path("totalTokenCount").asInt();
                            String modelVersion = root.path("modelVersion").asText();
                            double avgLogprobs = candidate.path("avgLogprobs").asDouble();

                            GeminiResponse geminiResponse = GeminiResponse.builder()
                                    .message(message)
                                    .promptTokens(promptTokens)
                                    .responseTokens(responseTokens)
                                    .totalTokens(totalTokens)
                                    .modelVersion(modelVersion)
                                    .avgLogprobs(avgLogprobs)
                                    .build();

                            return CompletableFuture.completedFuture(geminiResponse);
                        } catch (IOException e) {
                            CompletableFuture<GeminiResponse> errorFuture = new CompletableFuture<>();
                            errorFuture.completeExceptionally(e);
                            return errorFuture;
                        }
                    } else if (response.statusCode() == 429) {
                        CompletableFuture<GeminiResponse> errorFuture = new CompletableFuture<>();
                        errorFuture.completeExceptionally(new GeminiRateLimitException("Rate limit exceeded: 2000 requests per minute"));
                        return errorFuture;
                    } else {
                        CompletableFuture<GeminiResponse> errorFuture = new CompletableFuture<>();
                        errorFuture.completeExceptionally(
                                new RuntimeException("Error from API: HTTP " + response.statusCode() + " - " + response.body())
                        );
                        return errorFuture;
                    }
                }, (Executor) taskExecutor);
    }

    // A simple helper to escape user input for JSON strings
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }
}