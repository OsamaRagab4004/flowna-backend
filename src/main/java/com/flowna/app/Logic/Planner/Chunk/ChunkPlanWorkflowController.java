package com.flowna.app.Logic.Planner.Chunk;

import com.flowna.app.Agents.Gemini.GeminiResponse;
import com.flowna.app.Logic.Planner.DTO.PlannerRequest;
import com.flowna.app.Logic.ThreadChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/planner/chunk")

public class ChunkPlanWorkflowController {

    private final ChunkPlanWorkflowService chunkPlanWorkflowService;
    private final AsyncTaskExecutor taskExecutor; // Inject context-aware executor
    private final ThreadChecker threadChecker = new ThreadChecker();

    @Autowired
    public ChunkPlanWorkflowController(ChunkPlanWorkflowService chunkPlanWorkflowService, @Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.chunkPlanWorkflowService = chunkPlanWorkflowService;
        this.taskExecutor = taskExecutor;
    }

   @PostMapping("/generate")
   @Async(value = "taskExecutor") // Use the context-aware executor
   public CompletableFuture<ResponseEntity<GeminiResponse>> create(@RequestBody PlannerRequest plannerRequest) {
       threadChecker.checkCurrentAuthentication("create - Start");

       return chunkPlanWorkflowService.executeWorkflow(plannerRequest)
               .thenApplyAsync(geminiResponse -> {
                   threadChecker.checkCurrentAuthentication("create - thenApply (ResponseEntity)");
                   return ResponseEntity.ok(geminiResponse);
               }, taskExecutor)
               .exceptionallyAsync(ex -> {
                   threadChecker.checkCurrentAuthentication("create - exceptionally");
                   ex.printStackTrace();
                   return ResponseEntity.status(500).body(GeminiResponse.builder().message("An error occurred while processing the request.").build());
               }, taskExecutor);

}

}

/*
 public CompletableFuture<ResponseEntity<GeminiResponse>> create(@RequestBody PlannerRequest plannerRequest) {
        threadChecker.checkCurrentAuthentication("create - Start");

        return chunkPlanWorkflowService.executeWorkflow(plannerRequest)
                .thenApply(geminiResponse -> {
                    threadChecker.checkCurrentAuthentication("create - thenApply (ResponseEntity)");
                    return ResponseEntity.ok(geminiResponse);
                })
                .exceptionally(ex -> {
                    threadChecker.checkCurrentAuthentication("create - exceptionally");
                    ex.printStackTrace();
                    return ResponseEntity.status(500).body(GeminiResponse.builder().message("An error occurred while processing the request.").build());
                });
    }
/
 */