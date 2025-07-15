package com.flowna.app.studySquad.Agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterialResponse {

    private String definitions;
    private String qa;
    private String mindmap;
    private String stepbystep;



}
