package com.flowna.app.studySquad.Agents;


import com.flowna.app.studySquad.RoomLecture.RoomLectureRequest;
import com.flowna.app.studySquad.RoomLecture.RoomLectureResponse;
import com.flowna.app.studySquad.RoomLecture.RoomLectureService;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class LinkManagerService {

    private final StudyService studyService;
    private final PractiseService practiseService;
    private final RoomLectureService roomLectureService;


    public LinkManagerService(StudyService studyService, PractiseService practiseService, RoomLectureService roomLectureService) {
        this.studyService = studyService;
        this.practiseService = practiseService;
        this.roomLectureService = roomLectureService;
    }


    /**
     * Orchestrates the generation of study materials and saves them as a lecture.
     * It calls the StudyService to generate materials from a file, then saves the output
     * to the specified room using the RoomLectureService.
     *
     * @param request The study request containing file URI, room code, and generation flags.
     * @param userId  The ID of the user initiating the request.
     * @return A CompletableFuture containing the RoomLectureResponse with the new lecture's ID and title.
     */
    public CompletableFuture<RoomLectureResponse> generateStudyMaterialAndSave(StudyRequest request, int userId) {
        return studyService.chainOfStudyFunctions(userId, request)
                .thenApply(studyMaterialResponse -> {
                    System.out.println("Study material generated, now saving to the database.");

                    RoomLectureRequest lectureRequest = RoomLectureRequest.builder()
                            .roomJoinCode(request.getRoomJoinCode())
                            .title(request.getTitle())
                            .defintionsJson(studyMaterialResponse.getDefinitions())
                            .stepByStepJson(studyMaterialResponse.getStepbystep())
                            .qaJson(studyMaterialResponse.getQa())
                            .mindMapsJson(studyMaterialResponse.getMindmap())
                            .build();

                    return roomLectureService.addLectureToRoom(lectureRequest, userId);
                });
    }





}