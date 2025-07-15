package com.flowna.app.studySquad.QuestionMSQ;


                        import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionMSQ;
                        import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionSQRepository;
                        import com.flowna.app.studySquad.QuestionsCollector.QuestionsCollector;
                        import com.flowna.app.studySquad.QuestionsCollector.QuestionsCollectorRequest;
                        import com.flowna.app.studySquad.QuestionsCollector.QuestionsCollectorService;
                        import lombok.RequiredArgsConstructor;
                        import org.springframework.stereotype.Service;
                        import org.springframework.transaction.annotation.Transactional;

                        import java.util.List;
                        import java.util.Map;

                        @Service
                        @RequiredArgsConstructor
                        public class QuesitonMSQServie {

                            private final QuestionMSQRepository questionSQRepository;
                            private final QuestionsCollectorService questionsCollectorService;
                            private final AnswerOptionSQRepository answerOptionSQRepository;

                            /**
                             * Creates a new QuestionsCollector and saves a list of generated multiple-choice questions to the database.
                             * The questions are associated with the newly created QuestionsCollector.
                             *
                             * @param questionData A list of maps, where each map represents a question and its options.
                             *                     Expected map keys: "Question", "A", "B", "C", "D", "CorrectAnswer".
                             * @param collectorTitle        The title for the new QuestionsCollector.
                             * @param roomJoinCode The join code of the room to associate the new collector with.
                             */
                            @Transactional
                            public void saveGeneratedQuestions(List<Map<String, String>> questionData, String collectorTitle, String roomJoinCode) {
                                QuestionsCollectorRequest collectorRequest = QuestionsCollectorRequest.builder()
                                        .title(collectorTitle)
                                        .roomJoinCode(roomJoinCode)
                                        .build();
                                QuestionsCollector collector = questionsCollectorService.createQuestionsCollector(collectorRequest);

                                for (Map<String, String> data : questionData) {
                                    QuestionMSQ question = QuestionMSQ.builder()
                                            .questionText(data.get("Question"))
                                            .questionsCollector(collector)
                                            .build();

                                    questionSQRepository.save(question);

                                    String correctAnswerKey = data.get("CorrectAnswer");

                                    // Create and add answer options
                                    addAnswerOption(question, data.get("A"), "A".equals(correctAnswerKey));
                                    addAnswerOption(question, data.get("B"), "B".equals(correctAnswerKey));
                                    addAnswerOption(question, data.get("C"), "C".equals(correctAnswerKey));
                                    addAnswerOption(question, data.get("D"), "D".equals(correctAnswerKey));

                                    collector.getMcqQuestions().add(question);
                                }
                            }

                            private void addAnswerOption(QuestionMSQ question, String optionText, boolean isCorrect) {
                                if (optionText != null && !optionText.trim().isEmpty()) {
                                    AnswerOptionMSQ answerOption = AnswerOptionMSQ.builder()
                                            .answerText(optionText)
                                            .isCorrect(isCorrect)
                                            .questionSQ(question)
                                            .build();
                                    answerOptionSQRepository.save(answerOption);
                                    question.getAnswerOptionsSQ().add(answerOption);
                                }
                            }
                        }