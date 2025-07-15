package com.flowna.app.studySquad.RoomStudentScore;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RoomStudentScoreDTO {
    private int id;
    private int score;
    private int finalRoomScore;
    private String username;


}
