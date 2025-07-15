package com.flowna.app.studySquad.QuestionsCollector;

                                             import com.flowna.app.studySquad.AnswerOptionMSQ.AnswerOptionMSQ;
                                             import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
                                             import com.flowna.app.studySquad.Room.Room;
                                             import com.flowna.app.studySquad.Room.RoomRepository;
                                             import com.flowna.app.studySquad.StudentAnswer.StudentAnswer;
                                             import com.flowna.app.studySquad.StudentAnswer.StudentAnswerRepository;
                                             import jakarta.persistence.EntityNotFoundException;
                                             import lombok.RequiredArgsConstructor;
                                             import org.springframework.stereotype.Service;

                                             import java.util.ArrayList;
                                             import java.util.List;
                                             import java.util.Set;
                                             import java.util.stream.Collectors;

                                             @Service
                                             @RequiredArgsConstructor
                                             public class QuestionsCollectorService {

                                                 private final QuestionsCollectorRepository questionsCollectorRepository;
                                                 private final RoomRepository roomRepository;
                                                 private final StudentAnswerRepository studentAnswerRepository;

                                                 /**
                                                  * Creates a new QuestionsCollector for a given room.
                                                  *
                                                  * @param request The request object containing the room join code and title.
                                                  * @return The created QuestionsCollector object.
                                                  */
                                                 public QuestionsCollector createQuestionsCollector(QuestionsCollectorRequest request) {
                                                     Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode())
                                                             .orElseThrow(() -> new EntityNotFoundException("Room not found with join code: " + request.getRoomJoinCode()));

                                                     QuestionsCollector questionsCollector = QuestionsCollector.builder()
                                                             .title(request.getTitle())
                                                             .room(room)
                                                             .build();

                                                     return questionsCollectorRepository.save(questionsCollector);
                                                 }

                                                 /**
                                                  * Retrieves all questions for a specific QuestionsCollector.
                                                  *
                                                  * @param collectorId The ID of the QuestionsCollector.
                                                  * @return A list of QuestionMSQ objects.
                                                  */
                                                 public List<QuestionMSQ> getCollectorQuestions(Integer collectorId) {
                                                     QuestionsCollector collector = questionsCollectorRepository.findById(collectorId)
                                                             .orElseThrow(() -> new EntityNotFoundException("QuestionsCollector not found with ID: " + collectorId));

                                                     return new ArrayList<>(collector.getMcqQuestions());


                                                 }

                                                 /**
                                                  * Retrieves all QuestionsCollectors for a specific room.
                                                  *
                                                  * @param roomJoinCode The join code of the room.
                                                  * @return A list of QuestionsCollector objects for the specified room.
                                                  */
                                                 public List<QuestionsCollector> getCollectorsForRoom(String roomJoinCode) {
                                                     Room room = roomRepository.findByRoomJoinCode(roomJoinCode)
                                                             .orElseThrow(() -> new EntityNotFoundException("Room not found with join code: " + roomJoinCode));
                                                     return questionsCollectorRepository.findByRoom(room);
                                                 }

                                                 /**
                                                  * Retrieves a summary of answers for each question in a collector.
                                                  *
                                                  * @param collectorId The ID of the QuestionsCollector.
                                                  * @return A list of summaries, each containing question ID, correct answer ID, and a list of user answers with their ID and username.
                                                  */
                                                 public List<QuestionAnswersSummaryDTO> getQuestionAnswersSummary(Integer collectorId) {
                                                     QuestionsCollector collector = questionsCollectorRepository.findById(collectorId)
                                                             .orElseThrow(() -> new EntityNotFoundException("QuestionsCollector not found with ID: " + collectorId));

                                                     Set<QuestionMSQ> questions = collector.getMcqQuestions();

                                                     return questions.stream().map(question -> {
                                                         Integer correctAnswerId = question.getAnswerOptionsSQ().stream()
                                                                 .filter(AnswerOptionMSQ::isCorrect)
                                                                 .map(AnswerOptionMSQ::getId)
                                                                 .findFirst()
                                                                 .orElse(null);

                                                         List<AnswerOptionSummaryDTO> answerOptions = question.getAnswerOptionsSQ().stream()
                                                                 .map(option -> new AnswerOptionSummaryDTO(option.getId(), option.getAnswerText()))
                                                                 .collect(Collectors.toList());

                                                         List<StudentAnswer> answersForQuestion = studentAnswerRepository.findByQuestionSQ(question);
                                                         List<UserAnswerSummaryDTO> userAnswerSummaries = answersForQuestion.stream()
                                                                 .map(studentAnswer -> new UserAnswerSummaryDTO(
                                                                         studentAnswer.getAnswerText(),
                                                                         studentAnswer.getUser().getUniqueName()))
                                                                 .collect(Collectors.toList());

                                                         return QuestionAnswersSummaryDTO.builder()
                                                                 .questionId(question.getId())
                                                                 .questionText(question.getQuestionText())
                                                                 .correctAnswerId(correctAnswerId)
                                                                 .answerOptions(answerOptions)
                                                                 .userAnswers(userAnswerSummaries)
                                                                 .build();
                                                     }).collect(Collectors.toList());
                                                 }
                                             }