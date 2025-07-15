package com.flowna.app.studySquad.Agents;

                            import com.fasterxml.jackson.databind.ObjectMapper;
                            import org.springframework.beans.factory.annotation.Autowired;
                            import org.springframework.beans.factory.annotation.Value;
                            import org.springframework.core.io.ByteArrayResource;
                            import org.springframework.http.HttpEntity;
                            import org.springframework.http.HttpHeaders;
                            import org.springframework.http.MediaType;
                            import org.springframework.scheduling.annotation.Async;
                            import org.springframework.stereotype.Service;
                            import org.springframework.util.LinkedMultiValueMap;
                            import org.springframework.util.MultiValueMap;
                            import org.springframework.web.client.RestTemplate;
                            import org.springframework.web.multipart.MultipartFile;

                            import java.io.IOException;
                            import java.util.Arrays;
                            import java.util.Collections;
                            import java.util.HashMap;
                            import java.util.Map;
                            import java.util.concurrent.CompletableFuture;

                            @Service
                            public class GeminiApiService {

                                private final RestTemplate restTemplate;
                                private final String geminiApiKey;
                                private final GeminiApiExecutor apiExecutor;

                                private static final String GEMINI_UPLOAD_URL = "https://generativelanguage.googleapis.com/upload/v1beta/files?key=";
                                private static final String GEMINI_PRO_URL = "https://generativelanguage.googleapis.com/v1beta/models/"+Models.FLASH_2_5.getModelName()+":generateContent?key=";

                                @Autowired
                                public GeminiApiService(RestTemplate restTemplate, @Value("${gemini.api.key}") String geminiApiKey, GeminiApiExecutor apiExecutor) {
                                    this.restTemplate = restTemplate;
                                    this.geminiApiKey = geminiApiKey;
                                    this.apiExecutor = apiExecutor;
                                }

                                @Async
                                public CompletableFuture<String> uploadFileToGemini(MultipartFile file) {
                                    return CompletableFuture.supplyAsync(() -> {
                                        try {
                                            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                                            body.add("file", new ByteArrayResource(file.getBytes()) {
                                                @Override
                                                public String getFilename() {
                                                    return file.getOriginalFilename();
                                                }
                                            });

                                            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, new HttpHeaders());
                                            String url = GEMINI_UPLOAD_URL + geminiApiKey;

                                            String response = apiExecutor.postForObject(restTemplate, url, requestEntity, String.class, "uploadFileToGemini");
                                            System.out.println("File uploaded successfully: " + response);
                                            return response;
                                        } catch (IOException e) {
                                            throw new RuntimeException("Failed to read file for upload", e);
                                        } catch (Exception e) {
                                            throw new RuntimeException("API call failed for uploadFileToGemini after retries", e);
                                        }
                                    });
                                }

                                private HttpEntity<Map<String, Object>> createRequestEntity(Map<String, Object> requestBody) {
                                    HttpHeaders headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_JSON);
                                    return new HttpEntity<>(requestBody, headers);
                                }

                                private <T> CompletableFuture<T> generateContentAsync(String url, Map<String, Object> requestBody, Class<T> responseType, String context, boolean setThinkingConfig) {
                                    return CompletableFuture.supplyAsync(() -> {
                                        try {
                                            if (setThinkingConfig) {
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> genConfig = (Map<String, Object>) requestBody.get("generationConfig");
                                                if (genConfig == null) {
                                                    genConfig = new HashMap<>();
                                                    requestBody.put("generationConfig", genConfig);
                                                }
                                                genConfig.put("thinkingConfig", Map.of("thinkingBudget", 0));
                                            }

                                            HttpEntity<Map<String, Object>> requestEntity = createRequestEntity(requestBody);
                                            T response = apiExecutor.postForObject(restTemplate, url, requestEntity, responseType, context);

                                            ObjectMapper mapper = new ObjectMapper();
                                            String jsonResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                                            System.out.println("Content generated successfully for " + context + ": " + jsonResponse);

                                            return response;
                                        } catch (Exception e) {
                                            System.err.println("Error generating content for " + context + ": " + e.getMessage());
                                            throw new RuntimeException("API call failed for " + context + " after retries", e);
                                        }
                                    });
                                }

                                @Async
                                public CompletableFuture<GeminiResponse> generateContentWithFile(String fileUri, String mimeType, String prompt, boolean setThinkingConfig) {
                                    Map<String, Object> requestBody = new HashMap<>();
                                    mimeType = mimeType != null ? mimeType : "application/pdf"; // Default to PDF if not provided
                                    requestBody.put("contents", Collections.singletonList(Map.of("parts", Arrays.asList(
                                            Map.of("text", prompt),
                                            Map.of("file_data", Map.of("mime_type", mimeType, "file_uri", fileUri))
                                    ))));
                                    String url = GEMINI_PRO_URL + geminiApiKey;
                                    return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContentWithFile", setThinkingConfig);
                                }

                                @Async
                                public CompletableFuture<GeminiResponse> generateContentWithFileAndJsonResponse(String fileUri, String mimeType, String prompt, boolean setThinkingConfig) {
                                    Map<String, Object> requestBody = new HashMap<>();
                                    mimeType = mimeType != null ? mimeType : "application/pdf"; // Default to PDF if not provided
                                    requestBody.put("contents", Collections.singletonList(Map.of("parts", Arrays.asList(
                                            Map.of("text", prompt),
                                            Map.of("file_data", Map.of("mime_type", mimeType, "file_uri", fileUri))
                                    ))));
                                    requestBody.put("generationConfig", new HashMap<>(Map.of("responseMimeType", "application/json")));
                                    String url = GEMINI_PRO_URL + geminiApiKey;
                                    return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContentWithFileAndJsonResponse", setThinkingConfig);
                                }

                                @Async
                                public CompletableFuture<GeminiResponse> generateContent(String modelName, String prompt, String text, boolean setThinkingConfig) {
                                    Map<String, Object> requestBody = new HashMap<>();
                                    requestBody.put("contents", Collections.singletonList(Map.of(
                                            "role", "user",
                                            "parts", Collections.singletonList(Map.of("text", text))
                                    )));
                                    requestBody.put("systemInstruction", Map.of(
                                            "parts", Collections.singletonList(Map.of("text", prompt))
                                    ));
                                    String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
                                    return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContent", setThinkingConfig);
                                }

                                @Async
                                public CompletableFuture<GeminiResponse> generateContentWithCodeExecutionTool(String modelName, String prompt, String text, boolean setThinkingConfig) {
                                    Map<String, Object> requestBody = new HashMap<>();
                                    requestBody.put("contents", Collections.singletonList(Map.of(
                                            "role", "user",
                                            "parts", Collections.singletonList(Map.of("text", text))
                                    )));
                                    requestBody.put("systemInstruction", Map.of(
                                            "parts", Collections.singletonList(Map.of("text", prompt))
                                    ));
                                    requestBody.put("tools", Collections.singletonList(Map.of("codeExecution", new HashMap<>())));
                                    String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
                                    return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContentWithCodeExecutionTool", setThinkingConfig);
                                }

                                @Async
                                public CompletableFuture<GeminiResponse> generateContentWithJsonResponse(String modelName, String prompt, String text, boolean setThinkingConfig) {
                                    Map<String, Object> requestBody = new HashMap<>();
                                    requestBody.put("contents", Collections.singletonList(Map.of(
                                            "role", "user",
                                            "parts", Collections.singletonList(Map.of("text", text))
                                    )));
                                    requestBody.put("systemInstruction", Map.of(
                                            "parts", Collections.singletonList(Map.of("text", prompt))
                                    ));
                                    requestBody.put("generationConfig", new HashMap<>(Map.of("responseMimeType", "application/json")));
                                    String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
                                    return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContentWithJsonResponse", setThinkingConfig);
                                }


                            @Async
                            public CompletableFuture<GeminiResponse> generateContentWithJsonResponseAndSchemaStepbyStepFunc(String modelName, String prompt, String text, boolean setThinkingConfig) {
                                Map<String, Object> requestBody = new HashMap<>();
                                requestBody.put("contents", Collections.singletonList(Map.of(
                                        "role", "user",
                                        "parts", Collections.singletonList(Map.of("text", text))
                                )));
                                requestBody.put("systemInstruction", Map.of(
                                        "parts", Collections.singletonList(Map.of("text", prompt))
                                ));

                                Map<String, Object> schema = new HashMap<>();
                                schema.put("type", "object");
                                Map<String, Object> properties = new HashMap<>();
                                properties.put("Explanation", Map.of("type", "string"));
                                properties.put("Analogy", Map.of("type", "string"));
                                properties.put("Step-by-Step Real-World Example", Map.of("type", "array", "items", Map.of("type", "string")));
                                properties.put("Usage of the Concept", Map.of("type", "string"));
                                properties.put("Jargon Deconstruction and Explanation", Map.of("type", "array", "items", Map.of("type", "string")));
                                properties.put("Section Name", Map.of("type", "string"));
                                properties.put("rest of text", Map.of("type", "string"));
                                schema.put("properties", properties);
                                schema.put("required", Arrays.asList(
                                        "Explanation",
                                        "Analogy",
                                        "Step-by-Step Real-World Example",
                                        "Usage of the Concept",
                                        "Section Name"
                                ));

                                Map<String, Object> generationConfig = new HashMap<>();
                                generationConfig.put("responseMimeType", "application/json");
                                generationConfig.put("responseSchema", schema);

                                requestBody.put("generationConfig", generationConfig);

                                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
                                return generateContentAsync(url, requestBody, GeminiResponse.class, "generateContentWithJsonResponseAndSchema", setThinkingConfig);
                            }


                            @Async
                            public CompletableFuture<GeminiResponse> generateWithJsonResponseWithSchemaDefinitionsFunc(String modelName, String prompt, String text, boolean setThinkingConfig) {
                                Map<String, Object> requestBody = new HashMap<>();
                                requestBody.put("contents", Collections.singletonList(Map.of(
                                        "role", "user",
                                        "parts", Collections.singletonList(Map.of("text", text))
                                )));
                                requestBody.put("systemInstruction", Map.of(
                                        "parts", Collections.singletonList(Map.of("text", prompt))
                                ));

                                Map<String, Object> schema = new HashMap<>();
                                schema.put("type", "object");
                                Map<String, Object> properties = new HashMap<>();
                                properties.put("Concept", Map.of("type", "string"));
                                properties.put("The Core Definition", Map.of("type", "string"));
                                properties.put("Deeper Explanation for Beginners", Map.of("type", "string"));
                                properties.put("Notes and Simple Examples", Map.of("type", "array", "items", Map.of("type", "string")));
                                properties.put("Keywords and Tags", Map.of("type", "array", "items", Map.of("type", "string")));
                                properties.put("Simple Analogy", Map.of("type", "string"));
                                properties.put("Common Misconceptions", Map.of("type", "array", "items", Map.of("type", "string")));
                                properties.put("Bring it to Life Real-World Scenarios", Map.of("type", "array", "items", Map.of("type", "string")));
                                schema.put("properties", properties);
                                schema.put("required", Arrays.asList(
                                        "Concept",
                                        "The Core Definition",
                                        "Deeper Explanation for Beginners",
                                        "Notes & Simple Examples",
                                        "Keywords & Tags",
                                        "Simple Analogy",
                                        "Common Misconceptions",
                                        "Bring it to Life: Real-World Scenarios"
                                ));

                                Map<String, Object> generationConfig = new HashMap<>();
                                generationConfig.put("responseMimeType", "application/json");
                                generationConfig.put("responseSchema", schema);

                                requestBody.put("generationConfig", generationConfig);

                                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
                                return generateContentAsync(url, requestBody, GeminiResponse.class, "generateWithJsonResponseWithSchemaDefinitionsFunc", setThinkingConfig);
                            }



                            }