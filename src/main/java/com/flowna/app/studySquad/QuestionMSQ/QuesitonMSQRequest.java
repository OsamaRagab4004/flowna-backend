package com.flowna.app.studySquad.QuestionMSQ;

import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionSQRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuesitonMSQRequest {
    private QuestionType type;
    private String questionText;
    private String answerText;
    private Boolean trueFalseAnswer;
    private List<AnswerOptionSQRequest> answerOptions;

}
