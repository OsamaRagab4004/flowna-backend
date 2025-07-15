package com.flowna.app.studySquad.RoomGoal;

import com.flowna.app.studySquad.Room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomGaolRepository extends JpaRepository<RoomGaol, Integer> {
    // Custom query methods can be defined here if needed
    // For example, to find goals by room:
    List<RoomGaol> findByRoom(Room room);
}
