package com.flowna.app.Logic.Planner.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChunkPlan {
    private String milestones;
    private String days;






    @Override
    public String toString() {
        return
                "List of Milestones:'" + milestones + '\'' +
                ", List of Available Days:'" + days + '\'';
    }



}
