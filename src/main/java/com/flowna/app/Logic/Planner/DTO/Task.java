package com.flowna.app.Logic.Planner.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    private String taskTitle;
    private String taskDescription;
    private String taskPriority;
    private String taskDeadline;
    private String taskTimeEstimation;
    private String taskComplexity;
}
