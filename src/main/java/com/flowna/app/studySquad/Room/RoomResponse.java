package com.flowna.app.studySquad.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private int roomHoursGoal;
    private int roomStudyMinutes;
    private int roomPractiseMinutes;
    private String discordLink;

}
