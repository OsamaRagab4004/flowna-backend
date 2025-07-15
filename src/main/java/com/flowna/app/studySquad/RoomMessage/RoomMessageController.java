package com.flowna.app.studySquad.RoomMessage;


import com.flowna.app.email.EmailService;
import com.flowna.app.studySquad.Event;
import com.flowna.app.studySquad.EventType;
import com.flowna.app.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api/v1/squadgames/rooms/messages")
public class RoomMessageController {

    private final RoomMessageService roomMessageService;
    private final RoomMessageManager roomMessageManager; // Uncomment if you want to use RoomMessageManager
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService; // Assuming you have an EmailService for sending emails


    public RoomMessageController(RoomMessageService roomMessageService, RoomMessageManager roomMessageManager, SimpMessagingTemplate messagingTemplate, EmailService emailService) {
        this.roomMessageService = roomMessageService;
        this.roomMessageManager = roomMessageManager;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }


    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody  RoomMessageRequest request,
            @AuthenticationPrincipal User user) {

        try{
            // Save the message using the service
            RoomMessage savedMessage = roomMessageService.saveMessage(request, user.getId());

            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",user.getUsername() + " sent a message " + request);
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "Send a message in Room",
                    "messageTemplate.html",variables
            );

            //save the message in the RoomMessageManager
            roomMessageManager.addMessage(request.getRoomJoinCode(),
                    RoomMessageDTO.builder()
                            .id(savedMessage.getId())
                            .msgContent(savedMessage.getMsgContent())
                            .createdAt(savedMessage.getCreatedAt())
                            .username(user.getUniqueName())
                            .build());

            Set<RoomMessageDTO> messages = roomMessageManager.getMessages(request.getRoomJoinCode());

            Event event = Event.builder()
                    .eventType(EventType.SEND_MESSAGE)
                    .payload(messages)
                    .build();

            // Publish the event to the WebSocket (assuming you have a WebSocket publisher)
            messagingTemplate.convertAndSend("/topic/rooms/" + request.getRoomJoinCode(), event);



            // Return a successful response with the saved message
            return ResponseEntity.ok("Message sent successfully");
        } catch (IllegalArgumentException e) {
            // Handle the case where the room or user is not found
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions that may occur
            return ResponseEntity.status(500).body("An error occurred while sending the message: " + e.getMessage());
        }



    }



    @MessageMapping("/room-messages")
    public void getAllMessages(@RequestBody RoomMessageRequest request) {
        String roomJoinCode = request.getRoomJoinCode();
        try {
            // Atomically get the existing set or create a new one.
            Set<RoomMessageDTO> messages = roomMessageManager.getMessages(roomJoinCode);

            // If the set is empty, it implies this is the first access.
            // Load history from the database.
            if (messages.isEmpty()) {
                System.out.println("In-memory cache is empty for room: " + roomJoinCode + ". Fetching from DB.");
                try {
                    List<RoomMessageDTO> fetchedMessages = roomMessageService.getAllMessagesWithUserDetails(roomJoinCode);
                    // Add all fetched messages to the in-memory cache
                    messages.addAll(fetchedMessages);
                } catch (Exception e) {
                    System.err.println("Error fetching messages from DB for room: " + roomJoinCode + " - " + e.getMessage());
                    throw e; // Re-throw to handle in the catch block below
                }
            }

            Event event = Event.builder()
                    .eventType(EventType.GET_ALL_MESSAGES)
                    .payload(messages)
                    .build();

            messagingTemplate.convertAndSend("/topic/rooms/" + roomJoinCode, event);

        } catch (Exception e) {
            Event errorEvent = Event.builder()
                    .eventType(EventType.ERROR)
                    .payload("An error occurred while fetching messages: " + e.getMessage())
                    .build();
            messagingTemplate.convertAndSend("/topic/rooms/" + roomJoinCode, errorEvent);
        }
    }

}