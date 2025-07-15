package com.flowna.app.studySquad.Room;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRoomRequest {
    private String roomJoinCode; // The unique code to join the room
    private boolean archive; // Whether to archive the room or not
}
