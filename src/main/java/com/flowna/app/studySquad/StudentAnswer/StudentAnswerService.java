package com.flowna.app.studySquad.StudentAnswer;

                        import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionMSQ;
                        import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionSQRepository;
                        import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
                        import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQRepository;
                        import com.flowna.app.user.User;
                        import com.flowna.app.user.UserRepository;
                        import jakarta.persistence.EntityNotFoundException;
                        import lombok.RequiredArgsConstructor;
                        import org.springframework.stereotype.Service;
                        import org.springframework.transaction.annotation.Transactional;

                        import java.util.ArrayList;
                        import java.util.List;
                        import java.util.Optional;

                        @Service
                        @RequiredArgsConstructor
                        public class StudentAnswerService {

                            private final StudentAnswerRepository studentAnswerRepository;
                            private final QuestionMSQRepository questionMSQRepository;
                            private final UserRepository userRepository;
                            private final AnswerOptionSQRepository answerOptionMSQRepository;


                            @Transactional
                            public void saveStudentAnswers(Integer userId, StudentAnswersPayload payload) {
                                User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

                                List<StudentAnswer> studentAnswersToSave = new ArrayList<>();

                                for (StudentAnswerRequest answerRequest : payload.getAnswers()) {
                                    QuestionMSQ question = questionMSQRepository.findById(answerRequest.getQuestionId())
                                            .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));

                                    StudentAnswer studentAnswer = new StudentAnswer();
                                    studentAnswer.setUser(user);
                                    studentAnswer.setQuestionSQ(question);


                                    if (answerRequest.getSelectedOptionId() != null) {
                                        AnswerOptionMSQ answerOption = answerOptionMSQRepository.findById(answerRequest.getSelectedOptionId())
                                                .orElseThrow(() -> new EntityNotFoundException("AnswerOption not found with id: " + answerRequest.getSelectedOptionId()));
                                        studentAnswer.setAnswerText(answerOption.getAnswerText());
                                    } else {
                                        studentAnswer.setAnswerText(null);
                                    }

                                    studentAnswersToSave.add(studentAnswer);
                                }

                                studentAnswerRepository.saveAll(studentAnswersToSave);
                            }
                        }