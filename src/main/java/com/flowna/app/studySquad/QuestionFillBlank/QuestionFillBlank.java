package com.flowna.app.studySquad.QuestionFillBlank;


import com.flowna.app.studySquad.RoomLecture.RoomLecture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "question_fill_blank")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuestionFillBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_text", nullable = false)
    private String questionText;
    @Column(name = "answer", nullable = false)
    private String answer;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();




    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }




}




