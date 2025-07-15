package com.flowna.app.studySquad.Agents;

                        import com.flowna.app.studySquad.Prompts.*;
                        import com.flowna.app.studySquad.Room.Room;
                        import com.flowna.app.studySquad.Room.RoomRepository;
                        import com.flowna.app.user.User;
                        import com.flowna.app.user.UserRepository;
                        import org.json.JSONArray;
                        import org.json.JSONObject;
                        import org.springframework.stereotype.Service;

                        import java.util.Objects;
                        import java.util.concurrent.CompletableFuture;

                        import static com.flowna.app.studySquad.Agents.JsonExtractor.sanitizeValues;

@Service
                        public class StudyService {

                            private final GeminiApiService apiService;
                            private final UserRepository userRepository;
                            private final RoomRepository roomRepository;


                            public StudyService(GeminiApiService apiService, UserRepository userRepository, RoomRepository roomRepository) {
                                this.apiService = apiService;
                                this.userRepository = userRepository;
                                this.roomRepository = roomRepository;
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


                            public CompletableFuture<StudyMaterialResponse> chainOfStudyFunctions(
                                    int userId,
                                    StudyRequest request
                            ) {
                                System.out.println("chainOfStudyFunctions started for user: " + userId + " and room: " + request.getRoomJoinCode());

                                User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                                Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode())
                                        .orElseThrow(() -> new IllegalArgumentException("Room not found"));

                                if (!Objects.equals(room.getHost().getId(), user.getId())) {
                                    throw new IllegalArgumentException("User is not the host of the room");
                                }
                                System.out.println("User and Room validated successfully.");

                                final String fileUri = request.getFileUri();
                                final String mimeType = "application/pdf"; // Assuming the file is a PDF
                                final String prompt = AnalysePDF.CLEAN_TEXT_FOR_LEARN_WORKFLOW.getPrompt();
                                System.out.println("Requesting clean text from Gemini API for file: " + fileUri);
                                CompletableFuture<GeminiResponse> cleanTextFuture = apiService.generateContentWithFile(fileUri, mimeType, prompt, true);

                                return cleanTextFuture.thenCompose(response -> {
                                    System.out.println("Received response from Gemini API for file: " + fileUri);
                                    String cleanText = extractTextFromResponse(response);
                                    if (cleanText.isEmpty()) {
                                        System.err.println("Failed to extract valid text from the PDF response. The response was empty.");
                                        throw new IllegalStateException("Failed to extract valid text from the PDF response");
                                    }
                                    System.out.println("Successfully extracted clean text.");

                                    CompletableFuture<String> definitionsFuture;

                                    // #region : Defintions Generation
                                    if (request.isCreateDefinitions()) {
                                        System.out.println("Starting definitions generation...");
                                        definitionsFuture = apiService.generateContentWithJsonResponse(Models.FLASH_2_5.getModelName(), Definitions.SET_DEFINITIONS.getPrompt(), cleanText, true)
                                                .thenApply(res -> {
                                                    System.out.println("Definitions generation completed.");
                                                     String definitionsNotSenitized = extractTextFromResponse(res);
                                                    // Sanitize the definitions text
                                                    String definitionsSanitized = JsonFixer.fixJsonString(definitionsNotSenitized);
                                                    //System.out.println("Sanitized definitions text after json fix: " + "\n" + definitionsSanitized);
                                                    JSONArray jsonArrayDefinitions= new JSONArray(definitionsSanitized);
                                                    sanitizeValues(jsonArrayDefinitions);
                                                    String sanitizedDefinitions = jsonArrayDefinitions.toString();
                                                    System.out.println("------------------------------Definitions JSON ----------------------------------------");
                                                   // for (int i = 0; i < jsonArrayDefinitions.length(); i++) {
                                                      //JSONObject definition = jsonArrayDefinitions.getJSONObject(i);
                                                        //System.out.println(definition.toString(4));
                                                    //}


                                             return sanitizedDefinitions;
                                    });
                                    }else {
                                        System.out.println("Skipping definitions generation.");
                                        definitionsFuture = CompletableFuture.completedFuture(null);
                                    }
                                    CompletableFuture<String> mindmapFuture;
                                    if (request.isCreateMindmap()) {
                                        System.out.println("Starting mind map generation...");
                                        mindmapFuture = apiService.generateContent(Models.FLASH_2_5.getModelName(),
                                                        MindMap.GENERATE_MIND_MAP_TEXT.getPrompt() ,
                                                        cleanText, true)
                                                .thenCompose(mindmapResponse -> {
                                                    System.out.println("First step of mind map generation completed. Starting second step...");
                                                    String mindMapTextResponse = extractTextFromResponse(mindmapResponse);
                                                    return apiService.generateContent(Models.FLASH_2.getModelName(), MindMap.GENERATE_MIND_MAP_SYNTAX.getPrompt() + MindMap.MIND_MAP_EXAMPLES.getPrompt() + MindMap.MIND_MAP_EXAMPLES_TWO.getPrompt() + MindMap.MIND_MAP_EXAMPLES_THREE.getPrompt(), mindMapTextResponse, false).
                                                            thenCompose(brokeMindMapResponse -> {
                                                    System.out.println("Second step of mind map generation completed.");
                                                    String brokenMindMapText = extractTextFromResponse(brokeMindMapResponse);
                                                                return apiService.generateContent(Models.FLASH_2_5_LIGHT.getModelName(),
                                                                        MindMap.SANITIZE_MIND_MAP.getPrompt(),
                                                                        brokenMindMapText, true);

                                                            }).thenApply(res -> {
                                                                System.out.println("Mind map generation completed.");
                                                                String mindMapFinalText = extractTextFromResponse(res);
                                                                // Sanitize the mind map text
                                                                //System.out.println("Sanitized mind map text: " +"\n" + sanitizedMindMapText);
                                                                return MermaidSanitizer.sanitizeEscapedMermaidText(mindMapFinalText);
                                                            });
                                                });

                                    } else {
                                        System.out.println("Skipping mind map generation.");
                                        mindmapFuture = CompletableFuture.completedFuture(null);
                                    }

                                    // #region : Stepbystep Generation
                                    CompletableFuture<String> stepByStepFuture;
                                    if (request.isCreateStepbystep()) {
                                        System.out.println("Starting step-by-step explanation generation...");
                                        stepByStepFuture = apiService.generateContentWithJsonResponse(Models.FLASH_2_5.getModelName(),
                                                        Stepbystep.GENERATE_STEP_BY_STEP_EXPLANATION.getPrompt(),
                                                        cleanText, true)
                                                .thenApplyAsync(res -> {
                                                    int attempts = 0;
                                                    Exception lastException = null;
                                                    while (attempts < 3) {
                                                        try {
                                                            System.out.println("Step-by-step explanation generation completed.");
                                                            String stepByStepNotSanitized = extractTextFromResponse(res);
                                                            String stepByStepSanitized = JsonFixer.fixJsonString(stepByStepNotSanitized);
                                                            JSONObject jsonObjectStepByStep = new JSONObject(stepByStepSanitized);
                                                            sanitizeValues(jsonObjectStepByStep);
                                                            System.out.println("------------------------------Step by Step JSON ----------------------------------------");
                                                            return jsonObjectStepByStep.toString();
                                                        } catch (Exception e) {
                                                            attempts++;
                                                            lastException = e;
                                                            System.err.println("Failed to parse step-by-step JSON (attempt " + attempts + "): " + e.getMessage());
                                                            if (attempts < 3) {
                                                                // Recall the API and try again
                                                                res = apiService.generateContentWithJsonResponse(
                                                                        Models.FLASH_2_5.getModelName(),
                                                                        Stepbystep.GENERATE_STEP_BY_STEP_EXPLANATION.getPrompt(),
                                                                        cleanText, true
                                                                ).join();
                                                            }
                                                        }
                                                    }
                                                    throw new RuntimeException("Failed to parse step-by-step JSON after 3 attempts", lastException);
                                                });
                                    } else {
                                        System.out.println("Skipping step-by-step explanation generation.");
                                        stepByStepFuture = CompletableFuture.completedFuture(null);
                                    }

                                    // #region : QA Generation
                                    CompletableFuture<String> qaFuture;
                                    if (request.isCreateQA()) {
                                        System.out.println("Starting Q&A generation...");
                                        qaFuture = apiService.generateContentWithJsonResponse(Models.FLASH_2_5_LIGHT.getModelName(),
                                                        QA.GENERATE_QAS.getPrompt() ,
                                                        cleanText, true)
                                                .thenApply(res -> {
                                                    System.out.println("Q&A generation completed.");
                                                    String qaNotSanitized = extractTextFromResponse(res);
                                                    // Sanitize the Q&A text
                                                    JSONArray jsonArrayQA = new JSONArray(qaNotSanitized);
                                                    sanitizeValues(jsonArrayQA);
                                                    System.out.println("------------------------------Q&A JSON ----------------------------------------");
                                                    for (int i = 0; i < jsonArrayQA.length(); i++) {
                                                        JSONObject qa = jsonArrayQA.getJSONObject(i);
                                                        //System.out.println(qa.toString(4));
                                                    }
                                                    // Return the sanitized Q&A text
                                                    return jsonArrayQA.toString();
                                                });
                                    } else {
                                        System.out.println("Skipping Q&A generation.");
                                        qaFuture = CompletableFuture.completedFuture(null);
                                    }

                                    System.out.println("Waiting for all study material generation tasks to complete...");
                                    return CompletableFuture.allOf(definitionsFuture, mindmapFuture, stepByStepFuture, qaFuture)
                                            .thenApply(v -> {
                                                System.out.println("All generation tasks completed. Building final response.");
                                                return StudyMaterialResponse.builder()
                                                        .definitions(definitionsFuture.join())
                                                        .mindmap(mindmapFuture.join())
                                                        .stepbystep(stepByStepFuture.join())
                                                        .qa(qaFuture.join())
                                                        .build();
                                            });
                                });
                            }




}