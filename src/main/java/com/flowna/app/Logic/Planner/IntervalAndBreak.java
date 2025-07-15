package com.flowna.app.Logic.Planner;


import com.flowna.app.Agents.Gemini.GeminiModels;
import com.flowna.app.Agents.Gemini.GeminiRequest;
import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Agents.Gemini.GeminiService;
import com.flowna.app.Logic.Planner.DTO.DetailsOfDay;
import com.flowna.app.Logic.Prompts.Planner.General.AddTimeIntervalsAndBreak;
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
public class IntervalAndBreak {

    private static final Logger logger = LoggerFactory.getLogger(IntervalAndBreak.class);
    private GeminiService geminiService;
    private final AsyncTaskExecutor taskExecutor; // Inject context-aware executor

    @Autowired
    public IntervalAndBreak(GeminiService geminiService,  @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.geminiService = geminiService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Generates a plan based on the provided milestones and available days.
     *
     * @param  detailsOfDay
     * @return A string representation of the generated plan.
     */

@Async
public CompletableFuture<GeminiResponse> intervalAndBreakFunc(DetailsOfDay detailsOfDay, String plan) {
    System.out.println("Starting add time intervals and break...");
    ThreadChecker threadChecker = new ThreadChecker();
    threadChecker.checkCurrentAuthentication("intervalAndBreakFunc");
    GeminiRequest geminiRequest = GeminiRequest.builder()
            .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
            .user_prompt(detailsOfDay.toString() + "Plan:" + plan)
            .model_prompt(AddTimeIntervalsAndBreak.ADD_TIME_INTERVALS_AND_BREAK)
            .build();

    // Wrap the CompletableFuture with AsyncConfig.withSecurityContext

    return geminiService.getGeminiResponse(geminiRequest)
            .exceptionally(ex -> {
                // Handle any exceptions here
                ex.printStackTrace();
                logger.error("Error occurred while generating plan with intervals and breaks: {}", ex.getMessage());
                return null;
            });
}











}