package com.flowna.app.Logic.Planner.Chunk;

import com.flowna.app.Agents.Gemini.GeminiModels;
import com.flowna.app.Agents.Gemini.GeminiRequest;
import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Agents.Gemini.GeminiService;
import com.flowna.app.Logic.Planner.DTO.ChunkPlan;
import com.flowna.app.Logic.Prompts.Planner.Chunk.FullChunkPlan;
import com.flowna.app.Logic.ThreadChecker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class generateChunkPlan {

    private static final Logger logger = LoggerFactory.getLogger(generateChunkPlan.class);

    private GeminiService geminiService;
    private final AsyncTaskExecutor taskExecutor; // Inject the context-aware executor

    @Autowired
    public generateChunkPlan(GeminiService geminiService, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.geminiService = geminiService;
        this.taskExecutor = taskExecutor;
    }

    @Async
    public CompletableFuture<GeminiResponse> generateChunkPlanFunc(ChunkPlan chunkPlanDetails) {

        System.out.println("Inside generateChunkPlanFunc");
        ThreadChecker threadChecker = new ThreadChecker();
        threadChecker.checkCurrentAuthentication("generateChunkPlanFunc");
        GeminiRequest geminiRequest = GeminiRequest.builder()
                .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
                .user_prompt(chunkPlanDetails.toString())
                .model_prompt(FullChunkPlan.FULL_CHUNK_PLAN)
                .build();

        return geminiService.getGeminiResponse(geminiRequest)
                .exceptionally(ex -> {
                    // Handle any exceptions here
                    ex.printStackTrace();
                    logger.error("Error generating chunk plan: {}", ex.getMessage());
                    return null;
                });
    }
}