package com.flowna.app.studySquad.Room;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    private String roomJoinCode;
    private String name;
    private int studyHoursGoal;
    private int studyMinutes;
    private boolean studySession;
    private int practiceMinutes;
    private String discordLink;
}
