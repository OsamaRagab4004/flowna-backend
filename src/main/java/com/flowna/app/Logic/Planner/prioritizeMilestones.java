package com.flowna.app.Logic.Planner;

import com.flowna.app.Agents.Gemini.GeminiModels;
import com.flowna.app.Agents.Gemini.GeminiRequest;
import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Agents.Gemini.GeminiService;
import com.flowna.app.Logic.Prompts.Planner.General.PrioritizeMilestones;
import com.flowna.app.config.AsyncConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class prioritizeMilestones {

    private GeminiService geminiService;
    private static final Logger logger = LoggerFactory.getLogger(prioritizeMilestones.class);
    private final AsyncTaskExecutor taskExecutor; // Inject context-aware executor

    @Autowired
    public prioritizeMilestones(GeminiService geminiService, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.geminiService = geminiService;
        this.taskExecutor = taskExecutor;
    }



    /**
     * Generates sessions based on the provided milestones.
     *
     * @param Sessions The milestones for which to generate sessions.
     * @return A CompletableFuture containing the GeminiResponse
     */


    @Async
    public CompletableFuture<GeminiResponse> prioritizeMilestones(String Sessions) {
    System.out.println("Prioritize Milestones started");
    SecurityContextHolder.getContext().getAuthentication();
    GeminiRequest geminiRequest = GeminiRequest.builder()
            .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
            .user_prompt(Sessions)
            .model_prompt(PrioritizeMilestones.CATEGORIZE_MILESTONES)
            .build();

    // Wrap the CompletableFuture with AsyncConfig.withSecurityContext
    return geminiService.getGeminiResponse(geminiRequest)
                .exceptionally(ex -> {
                // Handle any exceptions here
                ex.printStackTrace();
                logger.error("Error in Prioritize Milestones: {}", ex.getMessage());
                return null;
            });

}





}
