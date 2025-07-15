package com.flowna.app.studySquad.RoomGoal;

import com.flowna.app.studySquad.Room.Room;
import com.flowna.app.studySquad.Room.RoomRepository;
import com.flowna.app.studySquad.Room.RoomService;
import com.flowna.app.user.User;
import com.flowna.app.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class RoomGaolService {

    private final RoomGaolRepository roomGaolRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomGaolManager roomGaolManager;

    public RoomGaolService(RoomGaolRepository roomGaolRepository, RoomRepository roomRepository, UserRepository userRepository, RoomGaolManager roomGaolManager) {
        this.roomGaolRepository = roomGaolRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomGaolManager = roomGaolManager;
    }


    public  RoomGaol addGoal(RoomGaolRequest request, int userId) {
        // Validate the room join code
        Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode()).orElseThrow(
                () -> new IllegalArgumentException("Room not found with join code: " + request.getRoomJoinCode())
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found ")
        );

        if (!room.getHost().getId().equals(user.getId())) {
                    throw new IllegalArgumentException("User is not the host of the room");
        }

        System.out.println("hours: " + request.getHours());
        System.out.println("minutes: " + request.getMinutes());
        RoomGaol roomGaol = RoomGaol.builder()
                .title(request.getGoalTitle())
                .hours(request.getHours())
                .minutes(request.getMinutes())
                .done(false)
                .room(room)
                .build();
        RoomGaol savedRoomGaol = roomGaolRepository.save(roomGaol);

        roomGaolManager.addGoal(
                RoomGaolRequest.builder()
                        .id(savedRoomGaol.getId())
                        .goalTitle(roomGaol.getTitle())
                        .done(request.isDone())
                        .hours(request.getHours())
                        .minutes(request.getMinutes())
                        .build(), request.getRoomJoinCode()
        );
        System.out.println("Current Room Goals: " + roomGaolManager.roomGoals);

        return roomGaolRepository.save(roomGaol);
    }


    public void removeGoal(RoomGaolRequest request, int userId) {
        // Validate the room join code
        Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode()).orElseThrow(
                () -> new IllegalArgumentException("Room not found with join code: " + request.getRoomJoinCode())
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found ")
        );

        if (!room.getHost().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User is not the host of the room");
        }

        roomGaolManager.removeGoal(request.getRoomJoinCode(), request);

        roomGaolRepository.deleteById(request.getId());
    }


    public void toggleGoal(RoomGaolRequest request, int userId) {
        // Validate the room join code
        Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode()).orElseThrow(
                () -> new IllegalArgumentException("Room not found with join code: " + request.getRoomJoinCode())
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found ")
        );

        if (!room.getHost().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User is not the host of the room");
        }

        RoomGaol roomGaol = roomGaolRepository.findById(request.getId()).orElseThrow(
                () -> new IllegalArgumentException("Goal not found with id: " + request.getId())
        );
        boolean currentDoneStatus = roomGaol.isDone();

        roomGaol.setDone(!currentDoneStatus);
        roomGaolRepository.save(roomGaol);

        roomGaolManager.toggleGoal(request.getRoomJoinCode(), request.getId(), !currentDoneStatus);
    }



}
