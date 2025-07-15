package com.flowna.app.studySquad.RoomGoal;

    import com.flowna.app.email.EmailService;
    import com.flowna.app.studySquad.Event;
    import com.flowna.app.studySquad.EventType;
    import com.flowna.app.studySquad.Room.Room;
    import com.flowna.app.studySquad.Room.RoomRepository;
    import com.flowna.app.user.User;
    import org.springframework.http.ResponseEntity;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.messaging.simp.SimpMessagingTemplate;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;

    @Controller
    @RequestMapping("/api/v1/squadgames/rooms/goals")
    public class RoomGaolController {

        private final RoomGaolManager roomGaolManager;
        private final RoomGaolService roomGaolService;
        private final SimpMessagingTemplate messagingTemplate;
        private final EmailService  emailService;
        private final RoomGaolRepository roomGaolRepository;
        private final RoomRepository roomRepository;


        public RoomGaolController(RoomGaolManager roomGaolManager, RoomGaolService roomGaolService, SimpMessagingTemplate messagingTemplate, EmailService emailService, RoomGaolRepository roomGaolRepository, RoomRepository roomRepository) {
            this.roomGaolManager = roomGaolManager;
            this.roomGaolService = roomGaolService;
            this.messagingTemplate = messagingTemplate;
            this.emailService = emailService;
            this.roomGaolRepository = roomGaolRepository;
            this.roomRepository = roomRepository;
        }




    @PostMapping("/all")
    public ResponseEntity<?> getGoals(@RequestBody RoomGaolRequest request) {
        String roomJoinCode = request.getRoomJoinCode();
        Set<RoomGaolRequest> goals = roomGaolManager.getGoals(roomJoinCode);

        if (goals == null || goals.isEmpty()) {
            Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
                    .orElse(null);
            if (room != null) {
                List<RoomGaol> goalsFromDb = roomGaolRepository.findByRoom(room);
                goalsFromDb.forEach(goal -> {
                    RoomGaolRequest goalRequest = RoomGaolRequest.builder()
                            .id(goal.getId())
                            .goalTitle(goal.getTitle())
                            .hours(goal.getHours())
                            .minutes(goal.getMinutes())
                            .done(goal.isDone())
                            .build();
                    roomGaolManager.addGoal(goalRequest, roomJoinCode);
                });
            }
            goals = roomGaolManager.getGoals(roomJoinCode);
        }

        System.out.println("Goals for room " + roomJoinCode + ": " + goals);
        return ResponseEntity.ok(goals);
    }


        @PostMapping("/add")
        public ResponseEntity<RoomGaol> addGoal(@RequestBody RoomGaolRequest request,
                                                @AuthenticationPrincipal User user) {
            RoomGaol roomGoal = roomGaolService.addGoal(request, user.getId());
            Set<RoomGaolRequest> goals = roomGaolManager.getGoals(request.getRoomJoinCode());
            Event event = Event.builder()
                    .eventType(EventType.ROOM_SESSIONS)
                    .payload(goals)
                    .build();
            messagingTemplate.convertAndSend("/topic/rooms/" + request.getRoomJoinCode(), event);

            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",user.getUsername() + " did add a new goal in room " + request);
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "Set a new goal in room",
                    "messageTemplate.html",variables
            );


            return ResponseEntity.ok(roomGoal);
        }


        @PostMapping("/toggle")
        public ResponseEntity<Void> toggleGoal(@RequestBody RoomGaolRequest request,
                                               @AuthenticationPrincipal User user) {
            roomGaolService.toggleGoal(request, user.getId());
            Set<RoomGaolRequest> goals = roomGaolManager.getGoals(request.getRoomJoinCode());
            Event event = Event.builder()
                    .eventType(EventType.ROOM_SESSIONS)
                    .payload(goals)
                    .build();
            messagingTemplate.convertAndSend("/topic/rooms/" + request.getRoomJoinCode(), event);

            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",user.getUsername() + " hasfinished a goal in room " + request);
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "Goal finished in room",
                    "messageTemplate.html",variables
            );

            return ResponseEntity.ok().build();
        }



        @PostMapping("/delete")
        public ResponseEntity<Void> delete(@RequestBody RoomGaolRequest request,
                                               @AuthenticationPrincipal User user) {
            roomGaolService.removeGoal(request, user.getId());
            Set<RoomGaolRequest> goals = roomGaolManager.getGoals(request.getRoomJoinCode());
            Event event = Event.builder()
                    .eventType(EventType.ROOM_SESSIONS)
                    .payload(goals)
                    .build();
            messagingTemplate.convertAndSend("/topic/rooms/" + request.getRoomJoinCode(), event);

            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",user.getUsername() + " hasfinished a goal in room " + request);
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "Goal deleted in room",
                    "messageTemplate.html",variables
            );

            return ResponseEntity.ok().build();
        }







    }