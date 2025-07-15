package com.flowna.app.Logic.Planner;

import com.flowna.app.Agents.Gemini.GeminiModels;
import com.flowna.app.Agents.Gemini.GeminiRequest;
import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Agents.Gemini.GeminiService;
import com.flowna.app.Logic.Planner.DTO.Session;
import com.flowna.app.Logic.Prompts.Planner.General.GenerateMilestonesAndSessions;
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
public class generateMilestonesAndSessions {

    private GeminiService geminiService;
    private static final Logger logger = LoggerFactory.getLogger(generateMilestonesAndSessions.class);
    private AsyncTaskExecutor taskExecutor; // Inject the context-aware executor

    @Autowired
    public generateMilestonesAndSessions(GeminiService geminiService, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.geminiService = geminiService;
        this.taskExecutor = taskExecutor;
    }

    ThreadChecker threadChecker = new ThreadChecker();



    @Async
    public CompletableFuture<GeminiResponse> generateMilestones(String Tasks) {
    GeminiRequest geminiRequest = GeminiRequest.builder()
            .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
            .user_prompt(Tasks)
            .model_prompt(GenerateMilestonesAndSessions.GENERATE_MILESTONES)
            .build();
    threadChecker.checkCurrentAuthentication("generateMilestones");
    System.out.println("Starting Creating Milestones");

    return geminiService.getGeminiResponse(geminiRequest)
            .exceptionally(ex -> {
                // Explicitly set the SecurityContext for this thread
                SecurityContextHolder.setContext(SecurityContextHolder.getContext());
                logger.error("Error generating milestones: {}", ex.getMessage());
                ex.printStackTrace();
                return null;
            });
}


    @Async
    public CompletableFuture<GeminiResponse> generateSessions(String Milestones, Session sessionDetails) {
        System.out.println("Starting Creating Sessions");
        threadChecker.checkCurrentAuthentication("generateSessions");
        String sessionDetailsString = sessionDetails.toString();
        String combinedInput = "Milestones : " + Milestones + "\n" + sessionDetailsString;

        GeminiRequest geminiRequest = GeminiRequest.builder()
                .model_typ(GeminiModels.GEMINI_2_0_FLASH.getModelName())
                .user_prompt(combinedInput)
                .model_prompt(GenerateMilestonesAndSessions.GENERATE_SESSIONS)
                .build();


         return geminiService.getGeminiResponse(geminiRequest)
                .exceptionally(ex -> {
                    logger.error("Error generating sessions: {}", ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });

    }
}