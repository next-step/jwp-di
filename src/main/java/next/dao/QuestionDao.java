package next.dao;

import next.model.Question;

import java.util.List;

public interface QuestionDao {

    public Question insert(Question question);

    public List<Question> findAll();

    public Question findById(long questionId);

    public void update(Question question);

    public void delete(long questionId);

    public void updateCountOfAnswer(long questionId);
}
