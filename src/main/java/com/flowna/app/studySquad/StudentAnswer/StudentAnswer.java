package com.flowna.app.studySquad.StudentAnswer;

import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
import com.flowna.app.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_answers")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = true)
    @Schema(description = "Answer of question by student")
    private String answerText;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_squad_id", nullable = false)
    private QuestionMSQ questionSQ;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;












}