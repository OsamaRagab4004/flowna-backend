package com.flowna.app.studySquad.Room;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomRequest {
    private String roomJoinCode; // The unique code to join the room
    private boolean rejoin; // Whether the user is rejoining the room or not

}
