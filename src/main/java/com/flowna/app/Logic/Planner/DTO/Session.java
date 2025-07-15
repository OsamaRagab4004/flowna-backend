package com.flowna.app.Logic.Planner.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    private String sessionTime;
    private String minimumSessionTime;








    @Override
public String toString() {
    return "session Time :'" + sessionTime + "\n" +
            "Minimum Session Time :'" + minimumSessionTime ;
}






}
