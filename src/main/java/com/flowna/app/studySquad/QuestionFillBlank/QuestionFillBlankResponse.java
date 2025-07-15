package com.flowna.app.studySquad.QuestionFillBlank;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFillBlankResponse {
    private Long id;
    private String questionText;
    private String answer;
    private LocalDateTime createdAt;

}