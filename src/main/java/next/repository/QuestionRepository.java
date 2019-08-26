package next.repository;


import next.model.Question;

import java.util.List;

public interface QuestionRepository {

    List<Question> findAll();

    Question findById(long questionId);

    Question insert(Question question);

    void update(Question question);

    void delete(long questionId);

    void updateCountOfAnswer(long questionId);
}
