package com.flowna.app.studySquad.RoomLecture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class RoomLectureRequest {

   private String roomJoinCode;
   private int lectureId;
    private String title;
    private String defintionsJson;
    private String stepByStepJson;
    private String qaJson;
    private String mindMapsJson;
    private String creationTime;

}