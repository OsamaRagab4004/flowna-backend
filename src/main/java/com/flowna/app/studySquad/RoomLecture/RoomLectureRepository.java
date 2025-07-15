package com.flowna.app.studySquad.RoomLecture;

import com.flowna.app.studySquad.Room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomLectureRepository extends JpaRepository<RoomLecture, Integer> {
    List<RoomLecture> findAllByRoom(Room room);
}