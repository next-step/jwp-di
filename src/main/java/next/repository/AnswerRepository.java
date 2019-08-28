package next.repository;

import next.model.Answer;

import java.util.List;

public interface AnswerRepository {

    Answer insert(final Answer answer);
    Answer findById(final long answerId);
    List<Answer> findAllByQuestionId(final long questionId);
    void delete(final Long answerId);
}
