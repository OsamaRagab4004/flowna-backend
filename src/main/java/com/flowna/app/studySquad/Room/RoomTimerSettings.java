package com.flowna.app.studySquad.Room;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RoomTimerSettings {
    private int timerDurationInSeconds; // Duration of the timer in seconds
    private String sessionGoals; // Goals for the study session
    private String roomJoinCode; // Unique code for the room
    private boolean timerEnabled;




}
