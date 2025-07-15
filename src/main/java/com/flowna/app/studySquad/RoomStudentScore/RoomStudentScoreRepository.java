package com.flowna.app.studySquad.RoomStudentScore;

import com.flowna.app.studySquad.Room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomStudentScoreRepository extends JpaRepository<RoomStudentScore, Integer> {

    List<RoomStudentScore> findRoomStudentScoreByRoom(Room room);

}
