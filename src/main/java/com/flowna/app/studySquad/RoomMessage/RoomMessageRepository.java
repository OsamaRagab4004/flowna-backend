package com.flowna.app.studySquad.RoomMessage;

import com.flowna.app.studySquad.Room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMessageRepository extends JpaRepository<RoomMessage, Integer> {

    List<RoomMessage> findAllByRoom(Room room);

}
