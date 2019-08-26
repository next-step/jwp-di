package next.repository;

import next.model.Answer;

import java.util.List;

public interface AnswerRepository {

    Answer findById(long answerId);

    Answer insert(Answer answer);

    void delete(long answerId);

    List<Answer> findAllByQuestionId(long questionId);
}