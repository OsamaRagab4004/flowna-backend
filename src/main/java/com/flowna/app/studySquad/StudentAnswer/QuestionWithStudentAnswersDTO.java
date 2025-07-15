package com.flowna.app.studySquad.StudentAnswer;

import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionWithStudentAnswersDTO {
    private String username;
    private String studentAnswer;
    private QuestionMSQ questionSQ;



}
