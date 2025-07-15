package com.flowna.app.Logic.Planner.Chunk;

import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Logic.Planner.DTO.ChunkPlan;
import com.flowna.app.Logic.Planner.DTO.PlannerRequest;
import com.flowna.app.Logic.ThreadChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import com.flowna.app.Logic.Planner.defineDaysWithCapacity;
import com.flowna.app.Logic.Planner.prioritizeMilestones;
import com.flowna.app.Logic.Planner.generateMilestonesAndSessions;
import com.flowna.app.Logic.Planner.IntervalAndBreak;

import java.util.concurrent.CompletableFuture;

@Service
public class ChunkPlanWorkflowService {

    @Autowired
    private generateChunkPlan generateChunkPlan;
    @Autowired
    private IntervalAndBreak intervalAndBreak;
    @Autowired
    private defineDaysWithCapacity defineDaysWithCapacity;
    @Autowired
    private prioritizeMilestones prioritizeMilestones;
    @Autowired
    private generateMilestonesAndSessions generateMilestonesAndSessions;

    private final AsyncTaskExecutor taskExecutor;

    public ChunkPlanWorkflowService(
            generateChunkPlan generateChunkPlan,
            IntervalAndBreak intervalAndBreak,
            defineDaysWithCapacity defineDaysWithCapacity,
            prioritizeMilestones prioritizeMilestones,
            generateMilestonesAndSessions generateMilestonesAndSessions,
            @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.generateChunkPlan = generateChunkPlan;
        this.intervalAndBreak = intervalAndBreak;
        this.defineDaysWithCapacity = defineDaysWithCapacity;
        this.prioritizeMilestones = prioritizeMilestones;
        this.generateMilestonesAndSessions = generateMilestonesAndSessions;
        this.taskExecutor = taskExecutor;
    }

    public CompletableFuture<GeminiResponse> executeWorkflow(PlannerRequest plannerRequest) {
    ThreadChecker threadChecker = new ThreadChecker();
    threadChecker.checkCurrentAuthentication("executeWorkflow - Start");

    // Step 1: Generate Milestones
    CompletableFuture<GeminiResponse> generateMilestonesFuture =
            generateMilestonesAndSessions.generateMilestones(plannerRequest.getAllTasksAsString())
                    .thenApply(geminiResponse -> {
                        threadChecker.checkCurrentAuthentication("executeWorkflow - thenApply (Generate Milestones)");
                        return geminiResponse;
                    });

    // Step 2: Generate Sessions
    CompletableFuture<GeminiResponse> generateSessionsFuture =
            generateMilestonesFuture.thenComposeAsync(geminiResponse -> {
                threadChecker.checkCurrentAuthentication("executeWorkflow - thenComposeAsync (Generate Sessions)");
                return generateMilestonesAndSessions.generateSessions(geminiResponse.getMessage(), plannerRequest.getSession());
            }, taskExecutor);

    // Step 3: Prioritize Milestones
    CompletableFuture<GeminiResponse> prioritizeMilestonesFuture =
            generateSessionsFuture.thenComposeAsync(geminiResponse -> {
                threadChecker.checkCurrentAuthentication("executeWorkflow - thenComposeAsync (Prioritize Milestones)");
                return prioritizeMilestones.prioritizeMilestones(geminiResponse.getMessage());
            }, taskExecutor);

    // Parallel Task Execution: Define Days with Capacity
    CompletableFuture<GeminiResponse> defineDaysWithCapacityFuture =
            defineDaysWithCapacity.defineDaysWithCapacityFunc(plannerRequest.getPlanDetails())
                    .thenApply(geminiResponse -> {
                        threadChecker.checkCurrentAuthentication("executeWorkflow - thenApply (Define Days with Capacity)");
                        return geminiResponse;
                    });

    // Step 4: Generate Chunk Plan
    CompletableFuture<GeminiResponse> generateChunkPlanFuture =
            prioritizeMilestonesFuture.thenCombineAsync(defineDaysWithCapacityFuture,
                    (prioritizeResponse, defineDaysResponse) -> {
                        threadChecker.checkCurrentAuthentication("executeWorkflow - thenCombineAsync (Generate Chunk Plan)");
                        ChunkPlan chunkPlan = ChunkPlan.builder()
                                .milestones(prioritizeResponse.getMessage())
                                .days(defineDaysResponse.getMessage())
                                .build();
                        return generateChunkPlan.generateChunkPlanFunc(chunkPlan);
                    }, taskExecutor).thenComposeAsync(chunkPlanFuture -> {
                        threadChecker.checkCurrentAuthentication("executeWorkflow - thenComposeAsync (Generate Chunk Plan Future)");
                        return chunkPlanFuture;
                    }, taskExecutor);

    // Step 5: Interval and Break
    return generateChunkPlanFuture.thenComposeAsync(geminiResponse -> {
        threadChecker.checkCurrentAuthentication("executeWorkflow - thenComposeAsync (Interval and Break)");
        return intervalAndBreak.intervalAndBreakFunc(plannerRequest.getDetailsOfDay(), geminiResponse.getMessage());
    }, taskExecutor);
  }


}