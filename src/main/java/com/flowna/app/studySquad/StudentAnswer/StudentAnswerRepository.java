package com.flowna.app.studySquad.StudentAnswer;

import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {
    List<StudentAnswer> findByQuestionSQ(QuestionMSQ questionSQ);
}