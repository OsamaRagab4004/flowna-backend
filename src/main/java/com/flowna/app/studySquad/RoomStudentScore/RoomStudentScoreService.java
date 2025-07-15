package com.flowna.app.studySquad.RoomStudentScore;


import com.flowna.app.studySquad.Room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomStudentScoreService {
    private final RoomStudentScoreRepository roomStudentScoreRepository;




    public List<RoomStudentScoreDTO> getRoomStudentScoresByRoomId(Room room) {
        List<RoomStudentScore> scores = roomStudentScoreRepository.findRoomStudentScoreByRoom(room);
        return scores.stream()
                .map(score -> RoomStudentScoreDTO.builder()
                        .id(score.getId())
                        .score(score.getScore())
                        .finalRoomScore(score.getFinalRoomScore())
                        .username(score.getUser().getUsername())
                        .build())
                .toList();
    }




}
