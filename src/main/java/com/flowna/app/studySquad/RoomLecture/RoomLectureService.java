package com.flowna.app.studySquad.RoomLecture;


import com.flowna.app.studySquad.Room.Room;
import com.flowna.app.studySquad.Room.RoomRepository;
import com.flowna.app.user.User;
import com.flowna.app.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomLectureService {

    private final RoomRepository roomRepository;
    private final RoomLectureRepository roomLectureRepository;
    private final UserRepository userRepository;


    /**
     * Adds a new lecture to a room. The user must be a member of the room.
     *
     * @param request The request object containing lecture details and the room join code.
     * @param userId  The ID of the user adding the lecture.
     * @return A RoomLectureResponse for the newly created lecture.
     * @throws EntityNotFoundException if no room is found with the provided join code or user is not found.
     * @throws AccessDeniedException if the user is not a member of the room.
     */

    @Transactional
    public RoomLectureResponse addLectureToRoom(RoomLectureRequest request, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with join code: " + request.getRoomJoinCode()));

    if (!user.getRooms().contains(room) || !room.getHost().equals(user)) {
        throw new AccessDeniedException("User must be a member and the host of this room.");
    }

        RoomLecture lecture = RoomLecture.builder()
                .title(request.getTitle())
                .DefinitionsJson(request.getDefintionsJson())
                .StepbystepJson(request.getStepByStepJson())
                .QAJson(request.getQaJson())
                .MindMapsJson(request.getMindMapsJson())
                .room(room)
                .build();

        RoomLecture savedLecture = roomLectureRepository.save(lecture);

        return mapToLectureResponse(savedLecture);
    }



    /**
     * Retrieves a single lecture by its ID. The user must be a member of the room containing the lecture.
     *
     * @param userId  The ID of the user requesting the lecture.
     * @return A RoomLectureResponse object with the lecture's details.
     * @throws EntityNotFoundException if no lecture is found with the provided ID or user is not found.
     * @throws AccessDeniedException if the user is not a member of the room.
     */
    @Transactional(readOnly = true)
    public RoomLectureResponse getLectureById(Integer lectureId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        RoomLecture lecture = roomLectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found with ID: " + lectureId));

        if (!lecture.getRoom().getUsers().contains(user)) {
            throw new AccessDeniedException("User is not a member of the room this lecture belongs to.");
        }

        RoomLectureResponse lectureResponse =  RoomLectureResponse.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .defintionsJson(lecture.getDefinitionsJson())
                .stepByStepJson(lecture.getStepbystepJson())
                .qaJson(lecture.getQAJson())
                .mindMapsJson(lecture.getMindMapsJson())
                .creationTime(lecture.getCreationTime().toString())
                .build();

        return lectureResponse;
    }





    /**
     * Retrieves all lectures for a given room. The user must be a member of the room.
     * @return A list of RoomLectureResponse objects, each containing the lecture's ID, title, and creation time.
     * @throws EntityNotFoundException if no room is found with the provided join code or user is not found.
     * @throws AccessDeniedException if the user is not a member of the room.
     */
    @Transactional(readOnly = true)
    public List<RoomLectureResponse> getAllLecturesForRoom(String roomJoinCode , int userId) {

        Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with join code: " + roomJoinCode));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        if (!user.getRooms().contains(room)) {
            throw new AccessDeniedException("User is not a member of the room.");
        }

        List<RoomLecture> lectures = roomLectureRepository.findAllByRoom(room);

        return lectures.stream()
                .map(this::mapToLectureResponse)
                .collect(Collectors.toList());
    }



    private RoomLectureResponse mapToLectureResponse(RoomLecture lecture) {
        return RoomLectureResponse.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .defintionsJson(lecture.getDefinitionsJson())
                .stepByStepJson(lecture.getStepbystepJson())
                .qaJson(lecture.getQAJson())
                .mindMapsJson(lecture.getMindMapsJson())
                .creationTime(lecture.getCreationTime().toString())
                .build();
    }




}