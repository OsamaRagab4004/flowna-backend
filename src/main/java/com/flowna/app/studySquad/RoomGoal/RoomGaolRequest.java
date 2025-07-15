package com.flowna.app.studySquad.RoomGoal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomGaolRequest {

    private int id;
    private String goalTitle;
    private int hours;
    private int minutes;
    private String roomJoinCode;
    private boolean done;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomGaolRequest that = (RoomGaolRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}