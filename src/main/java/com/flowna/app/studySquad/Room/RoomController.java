package com.flowna.app.studySquad.Room;


import com.flowna.app.email.EmailService;
import com.flowna.app.studySquad.Event;
import com.flowna.app.studySquad.EventType;
import com.flowna.app.studySquad.Room.Archived.ArchievedRoom;
import com.flowna.app.studySquad.Room.Archived.ArchievedRoomService;
import com.flowna.app.studySquad.UserDetails;
import com.flowna.app.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/api/v1/squadgames/rooms")
public class RoomController {


    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepository roomRepository;
    private final RoomUserManager roomUserManager;
    private final EmailService emailService;
    private final ArchievedRoomService archievedRoomService;

    public RoomController(RoomService roomService, SimpMessagingTemplate messagingTemplate, RoomRepository roomRepository, RoomUserManager roomUserManager, EmailService emailService, ArchievedRoomService archievedRoomService) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.roomRepository = roomRepository;
        this.roomUserManager = roomUserManager;
        this.emailService = emailService;
        this.archievedRoomService = archievedRoomService;
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg",roomRequest.getRoomJoinCode());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "New Room Created",
                "messageTemplate.html",variables
        );

        // Create the room
        Room room = roomService.createRoom(roomRequest, userDetails.getId());

        // Notify the user via WebSocket
        messagingTemplate.convertAndSend("/topic/rooms/" + room.getRoomJoinCode(), "Room created and subscribed!");

        ArchievedRoom roomToArchive = ArchievedRoom.builder()
                .name(room.getName())
                .roomJoinCode(room.getRoomJoinCode())
                .build();
        archievedRoomService.addArchivedRoomForUser(userDetails, roomToArchive);

        return ResponseEntity.ok(room);
    }


    /**
     * Endpoint to join a room using the room ID.
     *
     * @param joinRoomRequest The request containing the room ID.
     * @param userDetails     The authenticated user details.
     * @return ResponseEntity with the room details or an error message.
     * Return as JSON:
     * {
     * "eventType": "NEW_USER_JOINED",
     * "payload": [
     * {
     * "username": "hostUser",
     * "isHost": true,
     * "ready": false
     * },
     * {
     * "username": "newUser",
     * "isHost": false,
     * "ready": false
     * }
     * ]
     * }
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(
            @RequestBody JoinRoomRequest joinRoomRequest,
            @AuthenticationPrincipal User userDetails) {

        Set<UserDetails> usersOfRoom;
        try {
            // Join the user to the room
            usersOfRoom = roomService.JoinUserToRoom(joinRoomRequest.getRoomJoinCode(), userDetails.getId());
            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",userDetails.getUsername() + " has joined the room: " + joinRoomRequest.getRoomJoinCode());
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "New User Joined Room",
                    "messageTemplate.html",variables
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User already in the room or room is not online");
        }

        Event event;

        if (joinRoomRequest.isRejoin()) {

            event = Event.builder()
                    .eventType(EventType.ROOM_MEMBERS_LIST)
                    .payload(usersOfRoom)
                    .build();
            System.out.println("Rejoining room: " + joinRoomRequest.getRoomJoinCode() + ", users: " + usersOfRoom);

        } else {

            event = Event.builder()
                    .eventType(EventType.NEW_USER_JOINED)
                    .payload(usersOfRoom)
                    .build();
            System.out.println("Joining room: " + joinRoomRequest.getRoomJoinCode() + ", users: " + usersOfRoom);
            
            Room room = roomRepository.findByRoomJoinCode(joinRoomRequest.getRoomJoinCode())
                    .orElseThrow(() -> new IllegalStateException("Room not found"));
            ArchievedRoom roomToArchive = ArchievedRoom.builder()
                    .name(room.getName())
                    .roomJoinCode(room.getRoomJoinCode())
                    .build();
            archievedRoomService.addArchivedRoomForUser(userDetails, roomToArchive);

        }
        messagingTemplate.convertAndSend("/topic/rooms/" + joinRoomRequest.getRoomJoinCode(), event);


        return ResponseEntity.ok("User joined the room successfully");
    }


    @MessageMapping("/room-members")
    public Event joinRoomViaWebSocket(JoinRoomRequest joinRoomRequest) {

        Set<UserDetails> usersOfRoom = roomUserManager.getPlayersInRoom(joinRoomRequest.getRoomJoinCode());

        // Handle join logic, build event
        Event event = Event.builder()
                .eventType(EventType.ROOM_MEMBERS_LIST)
                .payload(usersOfRoom)
                .build();

        System.out.println("/room-members " + joinRoomRequest.getRoomJoinCode() + ", users: " + usersOfRoom);

        messagingTemplate.convertAndSend("/topic/rooms/" + joinRoomRequest.getRoomJoinCode(), event);
        return event;
    }


    @PostMapping("/toggle-ready")
    public ResponseEntity<?> toggleReadyStatus(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        // Toggle the ready status for the user in the specified room
        UserDetails user = roomService.toggleUserReady(roomRequest.getRoomJoinCode(), userDetails.getId());
        // Build event to notify all users in the room
        Event event = Event.builder()
                .eventType(EventType.PLAYER_READY)
                .payload(user)
                .build();

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg",userDetails.getUsername() + " has joined the room: " + userDetails.getUsername());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "User is Ready",
                "messageTemplate.html",variables
        );


        messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

        return ResponseEntity.ok(event);
    }


    @PostMapping("/set-host")
    public ResponseEntity<?> setNewHost(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        UserDetails newHost = roomService.setNewHost(roomRequest.getRoomJoinCode(), userDetails.getId());
        if (newHost == null) {
            return ResponseEntity.badRequest().body("Failed to set new host. User not in room or room not found.");
        }

        // Handle join logic, build event
        Event event = Event.builder()
                .eventType(EventType.ROOM_MEMBERS_LIST)
                .payload(newHost)
                .build();

        messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

        return ResponseEntity.ok(newHost);


    }


    @PostMapping("/leave-room")
    public ResponseEntity<?> leaveRoom(
            @RequestBody LeaveRoomRequest leaveRoomRequest,
            @AuthenticationPrincipal User userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        roomService.leaveRoom(leaveRoomRequest, userDetails.getId());
        Set<UserDetails> usersOfRoom = roomUserManager.getPlayersInRoom(leaveRoomRequest.getRoomJoinCode());
        Event event = Event.builder()
                .eventType(EventType.USER_LEFT)
                .payload(usersOfRoom)
                .build();
        System.out.println("leaveRoom " + leaveRoomRequest.getRoomJoinCode() + ", remaining users: " + usersOfRoom);
        messagingTemplate.convertAndSend("/topic/rooms/" + leaveRoomRequest.getRoomJoinCode(), event);
        return ResponseEntity.ok(event);
    }


// Timer Endpoints

    @PostMapping("/start-timer")
    public ResponseEntity<String> startTimer(
            @RequestBody RoomTimerSettings roomRequest,
            @AuthenticationPrincipal User userDetails) {

        // Start the timer for the room
        RoomTimerSettings settings = roomService.startTimer(roomRequest, userDetails.getId());

        // Notify all users in the room about the timer start
        // Handle join logic, build event
        Event event = Event.builder()
                .eventType(EventType.TIMER_STARTED)
                .payload(settings)
                .build();

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg",userDetails.getUsername() + " has joined the room: " +userDetails.getUsername() + " with timer settings: " + settings);
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "Started Timer",
                "messageTemplate.html",variables
        );


        messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

        return ResponseEntity.ok("Timer started for room: " + roomRequest.getRoomJoinCode());
    }


    @PostMapping("/stop-timer")
    public ResponseEntity<?> stopTimer(
            @RequestBody RoomTimerSettings roomRequest,
            @AuthenticationPrincipal User userDetails) {

        // Stop the timer for the room
        boolean stopped = roomService.stopTimer(roomRequest.getRoomJoinCode(), userDetails.getId());

        // Notify all users in the room about the timer stop
        // Handle join logic, build event
        Event event = Event.builder()
                .eventType(EventType.TIMER_STOPED)
                .payload("")
                .build();

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg",userDetails.getUsername() + " has joined the room: " + userDetails.getUsername());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "Stop Timer of the room",
                "messageTemplate.html",variables
        );


        messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

        if (!stopped) {
            return ResponseEntity.badRequest().body("Failed to stop timer. User not in room or room not found.");
        }

        return ResponseEntity.ok("Timer stopped for room: " + roomRequest.getRoomJoinCode());
    }


    @PostMapping("/set-room-hours-goal")
    public ResponseEntity<?> setRoomHoursGoal(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        // Set the room hours goal
        roomService.setRoamStudyHoursGoal(roomRequest, userDetails.getId());

        Event event = Event.builder()
                .eventType(EventType.SET_ROOM_HOURS_GOAL)
                .payload(roomRequest.getStudyHoursGoal())
                .build();

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg", userDetails.getUsername() + " has set the room hours goal to: " + roomRequest.getStudyHoursGoal());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "Set Room Hours Goal",
                "messageTemplate.html",variables
        );


        // Notify all users in the room about the updated room hours goal
        messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

        return ResponseEntity.ok("Successfully set room hours goal");
    }


    @PostMapping("/room-hours-goal")
    public ResponseEntity<?> getRoomHoursGoal(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        // Get the room hours goal
        Optional<Room> room = roomRepository.findByRoomJoinCode(roomRequest.getRoomJoinCode());

        if (room.isEmpty()) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        int hoursGoal = room.get().getRoomHoursGoal();
        RoomResponse roomResponse = RoomResponse.builder()
                .roomHoursGoal(hoursGoal)
                .build();


        return ResponseEntity.ok(roomResponse);

    }


    @PostMapping("/post-update")
    public ResponseEntity<String> postUpdateToAllRooms(
            @RequestBody UpdateRequest updateRequest,
            @AuthenticationPrincipal User userDetails) {

        // Fetch all rooms
        var rooms = roomRepository.findAll();

        // Broadcast the update to all users in each room
        rooms.forEach(room -> messagingTemplate.convertAndSend(
                "/topic/rooms/" + room.getRoomJoinCode(),
                rooms
        ));

        return ResponseEntity.ok("Update sent to all room users");
    }


    @PostMapping("/set-discord-link")
    public ResponseEntity<?> setDiscordLink(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        if (userDetails == null || userDetails.getId() == null) {
            return ResponseEntity.badRequest().body("User is not authenticated");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg",userDetails.getUsername());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "Discord Link Set",
                "messageTemplate.html",variables
        );


        try {
            Room updatedRoom = roomService.setDiscordLink(roomRequest, userDetails.getId());
            // Notify all users in the room about the updated Discord link
            Event event = Event.builder()
                    .eventType(EventType.SET_DISCORD_LINK)
                    .payload(updatedRoom.getDiscordLink())
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + updatedRoom.getRoomJoinCode(),
                    event
            );
            return ResponseEntity.ok(updatedRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/discord-link")
    public ResponseEntity<?> getDiscordLink(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        if (userDetails == null || userDetails.getId() == null) {
            return ResponseEntity.badRequest().body("User is not authenticated");
        }

        try {
            RoomResponse roomResponse = roomService.getDiscordLink(roomRequest.getRoomJoinCode(), userDetails.getId());
            return ResponseEntity.ok(roomResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }



    @PostMapping("/set-session-time")
        public ResponseEntity<?> setSessionTime(
                @RequestBody RoomRequest roomRequest,
                @AuthenticationPrincipal User userDetails) {

            if (userDetails == null || userDetails.getId() == null) {
                return ResponseEntity.badRequest().body("User is not authenticated");
            }

            try {
                RoomResponse response = roomService.setSessionTime(roomRequest, userDetails.getId());

                Event event = Event.builder()
                        .eventType(EventType.ROOM_SESSION_TIME_UPDATE)
                        .payload(response)
                        .build();

                Map<String, Object> variables = new HashMap<>();
                variables.put("msg",userDetails.getUsername() + " finished session time: " + response);
                emailService.sendSimpleEmail("webflowna@gmail.com",
                        "Complete Session Time",
                        "messageTemplate.html",variables
                );


                messagingTemplate.convertAndSend("/topic/rooms/" + roomRequest.getRoomJoinCode(), event);

                return ResponseEntity.ok(response);
            } catch (IllegalStateException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }


@PostMapping("/session-time")
    public ResponseEntity<?> getSessionTime(
            @RequestBody RoomRequest roomRequest,
            @AuthenticationPrincipal User userDetails) {

        if (userDetails == null || userDetails.getId() == null) {
            return ResponseEntity.badRequest().body("User is not authenticated");
        }

        try {
            RoomResponse response = roomService.getSessionTime(roomRequest.getRoomJoinCode(), userDetails.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    @MessageMapping("/typing")
    public Event typingUsers(
            @RequestBody TypingUser typingUser
    ) {


        // Handle join logic, build event
        Event event = Event.builder()
                .eventType(EventType.USER_TYPING)
                .payload(typingUser)
                .build();

        System.out.println("User typing in room: " + typingUser.getRoomJoinCode() + ", user: " + typingUser.getUsername());

        messagingTemplate.convertAndSend("/topic/rooms/" + typingUser.getRoomJoinCode(), event);
        return event;
    }



    @MessageMapping("/ping")
    public void handlePing(@Payload String pingData) {
        // Just receive the ping - connection test successful if this executes
        // Optional: log for debugging
        // System.out.println("Ping received: " + pingData);
    }


}