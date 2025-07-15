package com.flowna.app.studySquad.AnswerOptionMSQ;

            import com.fasterxml.jackson.annotation.JsonBackReference;
            import com.fasterxml.jackson.annotation.JsonProperty;
            import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
            import jakarta.persistence.*;
            import lombok.AllArgsConstructor;
            import lombok.Builder;
            import lombok.Data;
            import lombok.NoArgsConstructor;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @Entity
            @Table(name = "answer_option_sq")
            public class AnswerOptionMSQ {

                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Integer id;

                @Column(columnDefinition = "TEXT")
                private String answerText;

                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                private boolean isCorrect;

                @ManyToOne(fetch = FetchType.LAZY)
                @JoinColumn(name = "question_squad_id", nullable = false)
                @JsonBackReference
                private QuestionMSQ questionSQ;
            }