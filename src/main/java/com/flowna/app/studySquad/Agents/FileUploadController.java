package com.flowna.app.studySquad.Agents;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/api/v1/rooms/gemini")
public class FileUploadController {

    private final GeminiApiService geminiApiService;

    @Autowired
    public FileUploadController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    /**
     * Asynchronously handles the file upload request by calling the @Async service method.
     */
    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        return geminiApiService.uploadFileToGemini(file)
                .thenApply(ResponseEntity::ok) // If successful, wrap result in a 200 OK ResponseEntity
                .exceptionally(e -> { // If an exception occurred in the future
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error uploading file: " + e.getMessage());
                });
    }

    /**
     * Asynchronously handles the content generation request.
     */
    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<GeminiResponse>> generateContent(@RequestBody GenerateRequest generateRequest) {
        return geminiApiService.generateContentWithFile(
                        generateRequest.getFileUri(),
                        generateRequest.getMimeType(),
                        generateRequest.getPrompt(), false
                )
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
                });
    }
}

