package com.flowna.app.Logic.Planner.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsOfDay {
    private String startTimeOfDay;
    private String breakDuration;


    @Override
    public String toString() {
        return
                "Default Start Daily Time:[HH:MM] :  '" + startTimeOfDay + '\'' +
                ", Break Duration:  '" + breakDuration + '\'';
    }


}
