package com.flowna.app.Logic.Planner.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plan {

    private String maximumStudyHours;
    private String minimumStudyHours;
    private String planStartDate;
    private String planEndDate;
    private String blockedTime;
    private String offDays;





    @Override
    public String toString() {
        return
                "Maximum Study Hours per Day (Y): '" + maximumStudyHours + '\'' +
                ", Minimum Study Hours per Day (X): '" + minimumStudyHours + '\'' +
                ", Start Date:'" + planStartDate + '\'' +
                ", End Date:'" + planEndDate + '\'' +
                ", Blocked Times/Events:'" + blockedTime + '\'' +
                ", Off Days:'" + offDays + '\'';
    }
}
