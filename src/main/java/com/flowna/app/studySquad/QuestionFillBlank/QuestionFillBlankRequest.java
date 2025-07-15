package com.flowna.app.studySquad.QuestionFillBlank;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFillBlankRequest {
    private String questionText;
    private String answer;

}
