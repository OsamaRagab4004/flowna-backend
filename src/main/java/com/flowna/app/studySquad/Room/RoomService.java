package com.flowna.app.studySquad.Room;

import com.flowna.app.studySquad.UserDetails;
import com.flowna.app.user.User;
import com.flowna.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomUserManager roomUserManager;
    private final RoomTimerManager roomTimerManager;



public Room createRoom(RoomRequest roomRequest, int userId) {
    // Fetch the user by ID
    var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));

    // Create a new Room entity
    Room room = Room.builder()
            .name(roomRequest.getName())
            .roomJoinCode(generateRoomKey()) // Generate a unique join code
            .isOnline(true)
            .host(user) // Set the host of the room
            .users(new HashSet<>())
            .build();
    // Save the room first
    room = roomRepository.save(room);

    // Add the user to the room's users set
    room.getUsers().add(user);


    // Add the room to the user's rooms set
    user.getRooms().add(room);

    // Save the user to update the relationship
    userRepository.save(user);
    // Save the room to the database

    //save data to roomUserManager for real-time updates
    roomUserManager.addPlayerToRoom(room.getRoomJoinCode(), UserDetails.builder()
            .username(user.getUniqueName())
            .host(true)
            .ready(false)
            .studying(false)
            .score(0)
            .build());


    return room;
}




public Set<UserDetails> JoinUserToRoom(String roomJoinCode, int userId) {
    // Fetch the room by ID
    Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Fetch the user by ID
    var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user is already in the room
    if (room.getUsers().contains(user)) {
        throw new IllegalStateException("User is already in the room");
    }

    // Add the user to the room's users set
    room.getUsers().add(user);
    user.getRooms().add(room); // Add the room to the user's rooms set

    // Save the updated entities
    userRepository.save(user);
    roomRepository.save(room);
    //save data to roomUserManager for real-time updates
    boolean isHost = room.getHost() != null && Objects.equals(room.getHost().getId(), user.getId());

    roomUserManager.addPlayerToRoom(room.getRoomJoinCode(), UserDetails.builder()
            .username(user.getUniqueName())
            .host(isHost)
            .ready(false)
            .studying(false)
            .score(0)
            .build());

    return roomUserManager.getPlayersInRoom(room.getRoomJoinCode());
}


public UserDetails setNewHost(String roomCode, int userId) {
    // Fetch the room by ID
    Room room = roomRepository.findByRoomJoinCode(roomCode)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Fetch the user by ID
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user is in the room
    if (!room.getUsers().contains(user)) {
        throw new IllegalStateException("User is not in the room");
    }



    // Set the new host
    room.setHost(user);
    roomRepository.save(room);

    // Update the user's host status in the real-time manager
    roomUserManager.setPlayerHost(room.getRoomJoinCode(), user.getUniqueName(), true);

    Set<UserDetails> playersInRoom = roomUserManager.getPlayersInRoom(room.getRoomJoinCode());
    System.out.println("Updated players in room after setting new host: " + playersInRoom);

    return UserDetails.builder()
            .username(user.getUsername())
            .host(true)
            .build();
}


public UserDetails toggleUserReady(String roomCode, int userId) {
    // Fetch the room by join code
    Room room = roomRepository.findByRoomJoinCode(roomCode)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Fetch the user by ID
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));

    // Check if the user is in the room
    if (!room.getUsers().contains(user)) {
        throw new IllegalStateException("User is not in the room");
    }

    // Toggle the user's ready status in the real-time manager
    roomUserManager.toggleUserReady(room.getRoomJoinCode(), user.getUniqueName());

    return UserDetails.builder()
            .username(user.getUniqueName())
            .build();
}


public void leaveRoom(LeaveRoomRequest leaveRequest, int userId) {
    System.out.println("User with ID " + userId + " is trying to leave room with join code: " + leaveRequest.getRoomJoinCode());

    Room room = roomRepository.findByRoomJoinCode(leaveRequest.getRoomJoinCode())
            .orElseThrow(() -> new IllegalStateException("Room not found"));
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));

   if (room.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
       throw new IllegalStateException("User is not in the room");
   }



    if (!leaveRequest.isArchive()) {
        room.getUsers().remove(user);
        user.getRooms().remove(room);
        userRepository.save(user);
        roomRepository.save(room);
        System.out.println("User with ID " + userId + " has left the room without archiving.");



        roomRepository.save(room);
    }

    roomUserManager.removePlayerFromRoom(
            room.getRoomJoinCode(),
            user.getUniqueName()
    );
    System.out.println("User with ID " + userId + " has been removed from the real-time manager for room: " + leaveRequest.getRoomJoinCode());
}


public RoomResponse setSessionTime(RoomRequest request, int userId) {
    Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode())
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is the host of the room
    if (room.getHost() == null || room.getHost().getId() != userId) {
        throw new IllegalStateException("Only the host can set the session time");
    }

    if(request.isStudySession()) {
        final int STUDY_MINUTES = request.getStudyMinutes() + room.getStudyMinutes();
        room.setStudyMinutes(STUDY_MINUTES);
        roomRepository.save(room);
    } else {
        final int PRACTICE_MINUTES = request.getPracticeMinutes() + room.getLearningMinutes();
        room.setLearningMinutes(PRACTICE_MINUTES);
        roomRepository.save(room);
    }

    return RoomResponse.builder()
            .roomStudyMinutes(room.getStudyMinutes())
            .roomPractiseMinutes(room.getLearningMinutes())
            .build();
}



public RoomResponse getSessionTime(String roomJoinCode, int userId) {
        Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
                .orElseThrow(() -> new IllegalStateException("Room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!room.getUsers().contains(user)) {
            throw new IllegalStateException("User is not in the room");
        }

        return RoomResponse.builder()
                .roomStudyMinutes(room.getStudyMinutes())
                .roomPractiseMinutes(room.getLearningMinutes())
                .build();
    }



// timer settings for a room
public RoomTimerSettings startTimer(RoomTimerSettings timerSettings, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Room room = roomRepository.findByRoomJoinCode(timerSettings.getRoomJoinCode())
                .orElseThrow(() -> new IllegalStateException("Room not found"));
        // Check if the user is the host of the room
        if (room.getHost() == null || room.getHost().getId() != userId) {
            throw new IllegalStateException("Only the host can start the timer");
        }


        // Set the timer settings
        RoomTimerSettings settings = RoomTimerSettings.builder()
                .timerDurationInSeconds(timerSettings.getTimerDurationInSeconds())
                .sessionGoals(timerSettings.getSessionGoals())
                .roomJoinCode(timerSettings.getRoomJoinCode())
                .timerEnabled(true)
                .build();
        // Add the timer to the manager
        roomTimerManager.addTimer(timerSettings.getRoomJoinCode(), settings);
        return settings;
    }

public boolean stopTimer(String roomJoinCode, int userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is the host of the room
    if (room.getHost() == null || room.getHost().getId() != userId) {
        throw new IllegalStateException("Only the host can stop the timer");
    }


    // Stop the timer
    roomTimerManager.removeTimer(roomJoinCode);
    return true;
}




public void setRoamStudyHoursGoal(RoomRequest request, int userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode())
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is the host of the room
    if (room.getHost() == null || room.getHost().getId() != userId) {
        throw new IllegalStateException("Only the host can set study hours");
    }
    try {
        room.setRoomHoursGoal(request.getStudyHoursGoal());
        roomRepository.save(room);
    } catch (Exception e) {
        throw new IllegalStateException("Failed to set study hours: " + e.getMessage());
    }

}



public Room setDiscordLink(RoomRequest roomRequest, int userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    Room room = roomRepository.findByRoomJoinCode(roomRequest.getRoomJoinCode())
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is the host of the room
    if (room.getHost() == null || room.getHost().getId() != userId) {
        throw new IllegalStateException("Only the host can set the Discord link");
    }

    room.setDiscordLink(roomRequest.getDiscordLink());
    return roomRepository.save(room);
}



public RoomResponse getDiscordLink(String roomJoinCode, int userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is in the room
    if (!room.getUsers().contains(user)) {
        throw new IllegalStateException("User is not in the room");
    }

    RoomResponse roomResponse = RoomResponse.builder()
            .discordLink(room.getDiscordLink())
            .build();

    return roomResponse;
}

public int getRoomStudyHoursGoal(int roomId) {
    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalStateException("Room not found"));
    return room.getRoomHoursGoal();
}


public Set<User> getUsersInRoom(int roomId) {
    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalStateException("Room not found"));
    return room.getUsers();
}

// how to verify this action and secure endpoint ?
public Room archiveRoom(int roomId, int userId) {
    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalStateException("Room not found"));

    // Check if the user is the host of the room
    if (room.getHost() == null || room.getHost().getId() != userId) {
        throw new IllegalStateException("Only the host can archive the room");
    }
    // Set the room status to offline
    room.setIsOnline(false);

    // Save the updated room
    return roomRepository.save(room);
}











private String generateRoomKey() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomValue = String.valueOf((int) (Math.random() * 1_000_000));
        return "RoomKey-" + timestamp + "-" + randomValue;
}






}
