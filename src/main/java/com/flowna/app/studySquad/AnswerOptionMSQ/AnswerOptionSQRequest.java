package com.flowna.app.studySquad.AnswerOptionMSQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerOptionSQRequest {
    private String answerText;
    private Boolean isCorrect;

}

