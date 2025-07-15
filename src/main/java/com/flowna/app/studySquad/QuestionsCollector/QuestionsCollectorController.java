package com.flowna.app.studySquad.QuestionsCollector;

    import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.List;

    @RestController
    @RequestMapping("/api/v1/squadgames/questions-collector")
    @RequiredArgsConstructor
    public class QuestionsCollectorController {

        private final QuestionsCollectorService questionsCollectorService;

        /**
         * Retrieves all questions associated with a specific QuestionsCollector.
         *
         * @param collectorId The ID of the QuestionsCollector.
         * @return A ResponseEntity containing a list of questions.
         */
        @GetMapping("/{collectorId}/questions")
        public ResponseEntity<List<QuestionMSQ>> getCollectorQuestions(@PathVariable Integer collectorId) {
            List<QuestionMSQ> questions = questionsCollectorService.getCollectorQuestions(collectorId);
            return ResponseEntity.ok(questions);
        }


        /**
         * Retrieves all QuestionsCollectors for a specific room.
         *
         * @param roomJoinCode The join code of the room.
         * @return A ResponseEntity containing a list of QuestionsCollectors.
         */
        @GetMapping("/room/{roomJoinCode}")
        public ResponseEntity<List<QuestionsCollector>> getCollectorsForRoom(@PathVariable String roomJoinCode) {
            List<QuestionsCollector> collectors = questionsCollectorService.getCollectorsForRoom(roomJoinCode);
            return ResponseEntity.ok(collectors);
        }


        /**
         * Retrieves a summary of answers for each question in a collector.
         *
         * @param collectorId The ID of the QuestionsCollector.
         * @return A ResponseEntity containing a list of answer summaries.
         */
        @GetMapping("/{collectorId}/summary")
        public ResponseEntity<List<QuestionAnswersSummaryDTO>> getQuestionAnswersSummary(@PathVariable Integer collectorId) {
            List<QuestionAnswersSummaryDTO> summary = questionsCollectorService.getQuestionAnswersSummary(collectorId);
            return ResponseEntity.ok(summary);
        }


    }
