package com.flowna.app.studySquad.Agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowna.app.studySquad.Prompts.AnalysePDF;
import com.flowna.app.studySquad.Prompts.MCQ;
import com.flowna.app.studySquad.QuestionMSQ.QuesitonMSQServie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.flowna.app.studySquad.Agents.JsonExtractor.sanitizeValues;

@Service
public class PractiseService {

    private final GeminiApiService geminiApiService;
    private final ObjectMapper objectMapper;
    private final QuesitonMSQServie quesitonMSQServie;

    public PractiseService(GeminiApiService geminiService, ObjectMapper objectMapper, QuesitonMSQServie quesitonMSQServie) {
        this.geminiApiService = geminiService;
        this.objectMapper = objectMapper;
        this.quesitonMSQServie = quesitonMSQServie;
    }

    private String extractTextFromResponse(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty() ||
                response.getCandidates().get(0).getContent() == null ||
                response.getCandidates().get(0).getContent().getParts() == null ||
                response.getCandidates().get(0).getContent().getParts().isEmpty() ||
                response.getCandidates().get(0).getContent().getParts().get(0).getText() == null) {
            // Return empty string or throw an exception if the response is not as expected.
            return "";
        }
        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
    }

    public CompletableFuture<String> chainOfPractiseFunctions(String userId, GenerateRequest request) {
        System.out.println("chainOfPractiseFunctions started for user: " + userId);

        final String mimeType = "application/pdf"; // Assuming the file is a PDF

        CompletableFuture<GeminiResponse> generateJsonFuture = geminiApiService.generateContentWithFileAndJsonResponse(
                request.getFileUri(),
                mimeType,
                AnalysePDF.CLEAR_TEXT_FOR_MCQ_JSON_OUTPUT.getPrompt(), true);

        return generateJsonFuture.thenCompose(response -> {
            System.out.println("Received response from Gemini API for practice questions.");
            String jsonText = extractTextFromResponse(response);
            if (jsonText.isEmpty()) {
                System.err.println("Failed to extract valid JSON text from the response.");
                throw new IllegalStateException("Failed to extract valid JSON text from the response");
            }
            System.out.println("Successfully extracted JSON text. Now parsing to get content array.");
            try {

                // 1. PARSE the raw string into a JSONObject.
                JSONObject jsonObject = new JSONObject(jsonText);

                // 2. SANITIZE the values recursively. This modifies the jsonObject in place.
                sanitizeValues(jsonObject);

                // 3. RE-SERIALIZE the object to a clean string.
                String sanitizedJsonString = jsonObject.toString(4);

                System.out.println("--- Sanitized JSON String (Values Only) ---");
                System.out.println(sanitizedJsonString);

                JsonNode rootNode = objectMapper.readTree(sanitizedJsonString);
                JsonNode contentNode = rootNode.path("document_content");
                if (contentNode.isMissingNode() || !contentNode.isArray()) {
                    throw new IllegalStateException("'document_content' field is missing or not an array in the response.");
                }

                /*
                 * Logic for processing each chunk of content ***************************************************************************
                 *
                 * */

                List<JsonNode> allItems = new ArrayList<>();
                contentNode.forEach(allItems::add);

                final int numberOfThreads = 3;
                int totalSize = allItems.size();
                if (totalSize == 0) {
                    return CompletableFuture.completedFuture("[]");
                }
                int partitionSize = (int) Math.ceil((double) totalSize / numberOfThreads);

                List<List<JsonNode>> partitions = IntStream.range(0, (totalSize + partitionSize - 1) / partitionSize)
                        .mapToObj(i -> allItems.subList(i * partitionSize, Math.min((i + 1) * partitionSize, totalSize)))
                        .collect(Collectors.toList());

                System.out.println("--- Starting parallel MCQ generation in " + partitions.size() + " threads ---");

                List<CompletableFuture<JSONArray>> partitionFutures = partitions.stream()
                        .map(partition -> CompletableFuture.supplyAsync(() -> {
                            JSONArray partitionArray = new JSONArray();
                            for (JsonNode itemNode : partition) {
                                if (itemNode.isObject()) {
                                    JsonNode textNode = itemNode.path("text");
                                    if (!textNode.isMissingNode() && textNode.isTextual()) {
                                        String textValue = textNode.asText();
                                        String mcqPrompt;
                                        if (request.isPracticalQuestions()) {
                                            mcqPrompt = MCQ.PRACTICAL_MCQ_GENERATION.getPrompt();
                                        } else {
                                            mcqPrompt = MCQ.BASIC_MCQ_GENERATION.getPrompt();
                                        }

                                        GeminiResponse geminiResponse = geminiApiService.generateContentWithJsonResponse(
                                                Models.FLASH_2.getModelName(), mcqPrompt, textValue, false).join();

                                        String mcqJsonText = extractTextFromResponse(geminiResponse);
                                        if (mcqJsonText == null || mcqJsonText.trim().isEmpty()) {
                                            System.err.println("Warning: Received empty or null response from Gemini for a chunk.");
                                            continue;
                                        }

                                        String trimmedJson = mcqJsonText.trim();
                                        try {
                                            if (trimmedJson.startsWith("[")) {
                                                JSONArray jsonArrayChunk = new JSONArray(trimmedJson);
                                                sanitizeValues(jsonArrayChunk);
                                                for (int i = 0; i < jsonArrayChunk.length(); i++) {
                                                    partitionArray.put(jsonArrayChunk.get(i));
                                                }
                                            } else if (trimmedJson.startsWith("{")) {
                                                JSONObject jsonObjectChunk = new JSONObject(trimmedJson);
                                                sanitizeValues(jsonObjectChunk);
                                                partitionArray.put(jsonObjectChunk);
                                            } else {
                                                System.err.println("Warning: Gemini response is not a valid JSON object or array. Content: " + mcqJsonText);
                                            }
                                        } catch (JSONException e) {
                                            System.err.println("Error parsing JSON from Gemini chunk response: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                            System.out.println("A thread finished processing its partition.");
                            return partitionArray;
                        }))
                        .collect(Collectors.toList());

                CompletableFuture<Void> allOf = CompletableFuture.allOf(partitionFutures.toArray(new CompletableFuture[0]));

                return allOf.thenApply(v -> {
                    JSONArray finalJsonArray = new JSONArray();
                    partitionFutures.forEach(future -> {
                        try {
                            JSONArray partitionResult = future.join();
                            for (int i = 0; i < partitionResult.length(); i++) {
                                finalJsonArray.put(partitionResult.get(i));
                            }
                        } catch (Exception e) {
                            System.err.println("Error retrieving result from a partition future: " + e.getMessage());
                        }
                    });

                    System.out.println("All MCQ generation tasks are complete.");

                    // Save questions in batches of 25, each in a new collector
                    if (request.getRoomJoinCode() != null && !request.getRoomJoinCode().isEmpty()) {
                        final int BATCH_SIZE = 25;
                        List<Map<String, String>> questionBatch = new ArrayList<>();
                        int collectorNumber = 1;

                        for (int i = 0; i < finalJsonArray.length(); i++) {
                            try {
                                JSONObject questionObject = finalJsonArray.getJSONObject(i);
                                Map<String, Object> rawMap = questionObject.toMap();
                                Map<String, String> questionMap = new HashMap<>();
                                for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                                    questionMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                                }
                                questionBatch.add(questionMap);

                                // When batch is full, save it to a new collector
                                if (questionBatch.size() == BATCH_SIZE) {
                                    String collectorTitle = request.getTitle() + " - Part " + collectorNumber;
                                    quesitonMSQServie.saveGeneratedQuestions(new ArrayList<>(questionBatch), collectorTitle, request.getRoomJoinCode());
                                    System.out.println("Successfully saved collector '" + collectorTitle + "' with " + questionBatch.size() + " questions.");
                                    questionBatch.clear(); // Clear the batch for the next set
                                    collectorNumber++;
                                }
                            } catch (JSONException e) {
                                System.err.println("Error converting JSONObject to Map: " + e.getMessage());
                            }
                        }

                        // Save any remaining questions in the last batch to a new collector
                        if (!questionBatch.isEmpty()) {
                            String collectorTitle = request.getTitle() + " - Part " + collectorNumber;
                            quesitonMSQServie.saveGeneratedQuestions(questionBatch, collectorTitle, request.getRoomJoinCode());
                            System.out.println("Successfully saved the final collector '" + collectorTitle + "' with " + questionBatch.size() + " questions.");
                        }
                    } else {
                        System.out.println("Skipping save operation: roomJoinCode is not provided.");
                    }


                    System.out.println("--- Final Aggregated MCQs ---");
                    System.out.println(finalJsonArray.toString(4));

                    return finalJsonArray.toString(4);
                });

                /*
                 * **********************************************************************************************************************
                 * */

            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse inner JSON from Gemini response: " + e.getMessage());
                throw new RuntimeException("Failed to parse inner JSON from Gemini response", e);
            }
        });
    }
}