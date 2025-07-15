package com.flowna.app.studySquad.StudentAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerRequest {
    private Integer questionId;
    private Integer selectedOptionId;
}