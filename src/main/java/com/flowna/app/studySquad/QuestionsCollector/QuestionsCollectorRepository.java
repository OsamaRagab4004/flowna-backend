package com.flowna.app.studySquad.QuestionsCollector;

import com.flowna.app.studySquad.Room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QuestionsCollectorRepository extends JpaRepository<QuestionsCollector, Integer> {


    List<QuestionsCollector> findByRoom(Room room);


}
