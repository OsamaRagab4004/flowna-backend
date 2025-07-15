package com.flowna.app.studySquad.QuestionsCollector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerSummaryDTO {
    private String answerText;
    private String username;
}