package com.flowna.app.Agents.Deepseek;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class DeepseekService {

    //@Value("${deepseek.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public CompletableFuture<DeepseekResponse> getDeepseekResponse(DeepseekRequest deepseekRequest) {
        String requestBodyJson = "{"
                + "\"model\": \"deepseek-chat\","
                + "\"messages\": ["
                + "{"
                + "\"role\": \"system\","
                + "\"content\": \""+ escapeJson(deepseekRequest.getSystemPrompt()) +"\""
                + "},"
                + "{"
                + "\"role\": \"user\","
                + "\"content\": \"" + escapeJson(deepseekRequest.getUserPrompt()) + "\""
                + "}"
                + "],"
                + "\"stream\": false"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.deepseek.com/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode root = objectMapper.readTree(response.body());
                            JsonNode messageNode = root.path("choices").get(0).path("message");
                            String content = messageNode.path("content").asText();
                            int completionTokens = root.path("usage").path("completion_tokens").asInt();
                            int promptTokens = root.path("usage").path("prompt_tokens").asInt();
                            int totalTokens = root.path("usage").path("total_tokens").asInt();

                            DeepseekResponse deepseekResponse = DeepseekResponse.builder()
                                    .content(content)
                                    .completionTokens(completionTokens)
                                    .promptTokens(promptTokens)
                                    .totalTokens(totalTokens)
                                    .build();

                            return CompletableFuture.completedFuture(deepseekResponse);
                        } catch (IOException e) {
                            CompletableFuture<DeepseekResponse> errorFuture = new CompletableFuture<>();
                            errorFuture.completeExceptionally(e);
                            return errorFuture;
                        }
                    } else {
                        CompletableFuture<DeepseekResponse> errorFuture = new CompletableFuture<>();
                        errorFuture.completeExceptionally(
                                new RuntimeException("Error from API: HTTP " + response.statusCode() + " - " + response.body())
                        );
                        return errorFuture;
                    }
                });
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }
}