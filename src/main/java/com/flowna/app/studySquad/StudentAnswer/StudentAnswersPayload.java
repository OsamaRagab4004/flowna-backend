package com.flowna.app.studySquad.StudentAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswersPayload {
    private List<StudentAnswerRequest> answers;
}