package com.flowna.app.studySquad.QuestionsCollector;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class QuestionAnswersSummaryDTO {
        private Integer questionId;
        private String questionText;
        private Integer correctAnswerId;
        private List<AnswerOptionSummaryDTO> answerOptions;
        private List<UserAnswerSummaryDTO> userAnswers;
    }