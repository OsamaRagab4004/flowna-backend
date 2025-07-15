package com.flowna.app.studySquad.ExamScheduler;

import com.flowna.app.studySquad.Event;
import com.flowna.app.studySquad.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sends a message to a specific room's WebSocket topic.
     * @param roomId The ID of the room.
     * @param payload The  payload.
     */
    public void sendMessageToRoom(String roomId, EventType eventType, Object payload) {

        Event event = Event.builder()
                .eventType(eventType)
                .payload(payload)
                .build();
        String destination = "/topic/rooms/" + roomId;
        messagingTemplate.convertAndSend(destination, event);
    }




}