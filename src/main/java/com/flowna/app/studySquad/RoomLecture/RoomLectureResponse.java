package com.flowna.app.studySquad.RoomLecture;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomLectureResponse {

 private int lectureId;
private String title;
private String defintionsJson;
private String stepByStepJson;
private String qaJson;
private String mindMapsJson;
private String creationTime;









}