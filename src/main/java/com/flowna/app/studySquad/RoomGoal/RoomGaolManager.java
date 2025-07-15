package com.flowna.app.studySquad.RoomGoal;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomGaolManager {

    ConcurrentHashMap<String, Set<RoomGaolRequest>> roomGoals = new ConcurrentHashMap<>();


    public void addGoal(RoomGaolRequest goal, String roomJoinCode) {
        roomGoals.computeIfAbsent(roomJoinCode, k -> ConcurrentHashMap.newKeySet()).add(goal);
    }

  public Set<RoomGaolRequest> getGoals(String roomId) {
      return roomGoals.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
  }

  public void toggleGoal(String roomId, int goalId, boolean done) {
        Set<RoomGaolRequest> goals = roomGoals.get(roomId);
        if (goals != null) {
            goals.stream()
                    .filter(g -> g.getId() == goalId)
                    .findFirst()
                    .ifPresent(g -> g.setDone(done));
        }

  }



    public void removeGoal(String roomId, RoomGaolRequest goal) {
        Set<RoomGaolRequest> goals = roomGoals.get(roomId);
        if (goals != null) {
            goals.removeIf(g -> g.getId() == goal.getId());
            if (goals.isEmpty()) {
                roomGoals.remove(roomId);
            }
        }
    }



}
