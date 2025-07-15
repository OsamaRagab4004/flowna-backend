package com.flowna.app.studySquad.Agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequest {

    private String fileUri;
    private String mimeType;
    private String  title;
    private  boolean  practicalQuestions;
    private String roomJoinCode;
    private String prompt;

}