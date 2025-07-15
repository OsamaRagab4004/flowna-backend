package com.flowna.app.studySquad.QuestionMSQ;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionMSQ;
    import com.flowna.app.studySquad.QuestionsCollector.QuestionsCollector;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.ArrayList;
    import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "questions_mcq_squad")
    public class QuestionMSQ {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(columnDefinition = "TEXT")
        private String questionText;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "collector_id", nullable = false)
        @JsonBackReference
        private QuestionsCollector questionsCollector;

        @Builder.Default
        @OneToMany(mappedBy = "questionSQ", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        private List<AnswerOptionMSQ> answerOptionsSQ = new ArrayList<>();
    }