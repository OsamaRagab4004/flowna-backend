package com.flowna.app.studySquad.ExamScheduler;

        import com.flowna.app.studySquad.Event;
        import com.flowna.app.studySquad.EventType;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.scheduling.TaskScheduler;
        import org.springframework.stereotype.Service;

        import java.time.Instant;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.concurrent.ScheduledFuture;

        @Service
        public class DynamicSchedulerService {

            private final TaskScheduler taskScheduler;
            private final WebSocketMessageService webSocketMessageService;
            private final ExamSchedulerManager examSchedulerManager;
            private final SimpMessagingTemplate messagingTemplate;


            // A map to hold references to scheduled tasks, allowing for cancellation
            private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

            @Autowired
            public DynamicSchedulerService(@Qualifier("webSocketScheduler") TaskScheduler taskScheduler, WebSocketMessageService webSocketMessageService, ExamSchedulerManager examSchedulerManager, SimpMessagingTemplate messagingTemplate) {
                this.taskScheduler = taskScheduler;
                this.webSocketMessageService = webSocketMessageService;
                this.examSchedulerManager = examSchedulerManager;
                this.messagingTemplate = messagingTemplate;
            }

            public void scheduleMessage(String roomId,   int CollectorId) {
                System.out.println("--- Scheduling exam for room: " + roomId + " with collector ID: " + CollectorId + " ---");

                // Calculate the execution time based on the current time and the exam duration
                Instant executionTime = Instant.now().plusSeconds(35*60L);
                System.out.println("Exam finish time set to: " + executionTime);


                webSocketMessageService.sendMessageToRoom(roomId,EventType.EXAM_STARTED ,CollectorId);
                System.out.println("EXAM_STARTED event sent to room: " + roomId);

                // Define the task to be executed.
                // This is the logic that will run at the scheduled time.

                // #payload :: must be correct answers for questions , so in frotnend we can compare with user answers
                Runnable task = () -> {
                    System.out.println("--- Executing scheduled task for room: " + roomId + " ---");
                    webSocketMessageService.sendMessageToRoom(roomId,EventType.EXAM_FINISHED ,"Done!");
                    System.out.println("EXAM_FINISHED event sent to room: " + roomId);
                };

                // Schedule the task to run at the specified time
                ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, executionTime);

                // Store the future task if you need to cancel it later
                scheduledTasks.put(roomId, scheduledTask);
                System.out.println("Task scheduled successfully for room: " + roomId);
            }




            public void cancelScheduledMessage(String roomId) {
                ScheduledFuture<?> scheduledTask = scheduledTasks.get(roomId);
                if (scheduledTask != null) {
                    // true to interrupt the thread if it's running.
                    // false is usually sufficient.
                    scheduledTask.cancel(false);
                    scheduledTasks.remove(roomId);
                    System.out.printf("Cancelled scheduled task with id: %s%n", roomId);
                }
            }


            public boolean isExamOngoing(String roomId) {
                 if(examSchedulerManager.getParticipantCount(roomId) > 0) {
                    System.out.println("Exam is ongoing for room: " + roomId);
                    return true;
                 }
                 System.out.println("No ongoing exam for room: " + roomId);
                 cancelScheduledMessage(roomId);
                 return false;
             }




        }