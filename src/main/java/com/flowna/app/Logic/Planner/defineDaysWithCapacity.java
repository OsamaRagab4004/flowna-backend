package com.flowna.app.Logic.Planner;


import com.flowna.app.Agents.Gemini.GeminiModels;
import com.flowna.app.Agents.Gemini.GeminiRequest;
import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Agents.Gemini.GeminiService;
import com.flowna.app.Logic.Planner.DTO.Plan;
import com.flowna.app.Logic.Prompts.Planner.General.DefineDaysWithCapacity;
import com.flowna.app.Logic.ThreadChecker;
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
public class defineDaysWithCapacity {

    private static final Logger logger = LoggerFactory.getLogger(defineDaysWithCapacity.class);
    private GeminiService geminiService;
    private final AsyncTaskExecutor taskExecutor; // Inject context-aware executor

    @Autowired
    public defineDaysWithCapacity(GeminiService geminiService, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.geminiService = geminiService;
        this.taskExecutor = taskExecutor;
    }


    @Async
   public CompletableFuture<GeminiResponse> defineDaysWithCapacityFunc(Plan planDetails) {
    System.out.println("Inside defineDaysWithCapacityFunc");
    ThreadChecker threadChecker = new ThreadChecker();
    threadChecker.checkCurrentAuthentication("defineDaysWithCapacityFunc");
    GeminiRequest geminiRequest = GeminiRequest.builder()
            .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
            .user_prompt(planDetails.toString())
            .model_prompt(DefineDaysWithCapacity.DEFINE_DAYS_WITH_CAPACITY)
            .build();

    // Wrap the CompletableFuture with AsyncConfig.withSecurityContext

      return geminiService.getGeminiResponse(geminiRequest)
            .exceptionally(ex -> {
                ex.printStackTrace();
                logger.error("Error defining days with capacity: {}", ex.getMessage());
                return null;
            });

}




}
