package com.flowna.app.studySquad.ExamScheduler;

    import com.flowna.app.studySquad.EventType;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ExamShcedulerRequest {

        private String roomId;
        private int examDurationInMinutes;
        private int collectorId;
        private String username;

    }