package com.flowna.app.studySquad.Agents;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StudyRequest {

    private String roomJoinCode;
    private String fileUri;
    private String title;
    private boolean createMindmap;
    private boolean createDefinitions;
    private boolean createQA;
    private boolean createStepbystep;





}
