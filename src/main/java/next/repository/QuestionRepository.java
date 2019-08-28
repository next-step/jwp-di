package next.repository;

import next.model.Question;

import java.util.List;

public interface QuestionRepository {

    Question insert(final Question question);
    List<Question> findAll();
    Question findById(final long questionId);
    void update(final Question question);
    void delete(final long questionId);
    void updateCountOfAnswer(final long questionId);
    void deleteAll();
}
