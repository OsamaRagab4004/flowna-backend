package com.flowna.app.Logic.Planner.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlannerRequest {
    private List<Task> task;
    private Session session;
    private Plan planDetails;

    private DetailsOfDay detailsOfDay;




    /**
     * Retrieves all tasks as a single String.
     *
     * @return A concatenated String of all tasks.
     */
    public String getAllTasksAsString() {
        return task != null ? task.stream()
                .map(Task::toString)
                .reduce((t1, t2) -> t1 + "\n" + t2)
                .orElse("") : "";
    }
}
