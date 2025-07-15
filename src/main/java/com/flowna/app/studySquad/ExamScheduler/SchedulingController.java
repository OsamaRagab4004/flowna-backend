package com.flowna.app.studySquad.ExamScheduler;


import com.flowna.app.studySquad.Event;
import com.flowna.app.studySquad.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulingController {

    private final DynamicSchedulerService dynamicSchedulerService;
    private final ExamSchedulerManager examSchedulerManager;
    private final SimpMessagingTemplate messagingTemplate;



    /**
     * Schedules a message to be sent at a later time based on the exam duration.
     * @param request The request containing scheduling details.
     * @return A response entity indicating success.
     */
    @PostMapping("/schedule")
    public ResponseEntity<Void> scheduleMessage(@RequestBody ExamShcedulerRequest request) {
        dynamicSchedulerService.scheduleMessage(
                request.getRoomId(),
                request.getCollectorId()
        );

        return ResponseEntity.ok().build();
    }



    @PostMapping("/join")
    public ResponseEntity<Void> joinExam(@RequestBody ExamShcedulerRequest request) {
        examSchedulerManager.addPlayerToExam(request.getRoomId(), request.getUsername());
        System.out.println("User " + request.getUsername() + " joined exam in room: " + request.getRoomId());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/leave")
    public ResponseEntity<Void> leaveExam(@RequestBody ExamShcedulerRequest request) {
        examSchedulerManager.removePlayerFromExam(request.getRoomId(), request.getUsername());
        System.out.println("User " + request.getUsername() + " left exam in room: " + request.getRoomId());
        return ResponseEntity.ok().build();
    }



    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkExamStatus(@RequestBody ExamShcedulerRequest request) {
        boolean isGoing = dynamicSchedulerService.isExamOngoing(request.getRoomId());
        if (!isGoing) {
            Event event = Event.builder()
                    .eventType(EventType.EXAM_IS_NOT_GOING)
                    .payload(isGoing)
                    .build();
            String destination = "/topic/rooms/" + request.getRoomId();
            messagingTemplate.convertAndSend(destination, event);
            System.out.println("[WARNING!!] Exam is not ongoing for room: " + request.getRoomId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("isGoing", isGoing);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a previously scheduled message for a specific room.
     * @param roomId The ID of the room for which to cancel the task.
     * @return A response entity indicating success.
     */
    @PostMapping("/cancel/{roomId}")
    public ResponseEntity<Void> cancelScheduledMessage(@PathVariable String roomId) {
        dynamicSchedulerService.cancelScheduledMessage(roomId);
        return ResponseEntity.ok().build();
    }







}