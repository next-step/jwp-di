package next.service;

import core.annotation.Inject;
import next.dao.QuestionDao;
import next.model.Question;

import java.util.List;
import java.util.Optional;

public class QuestionService {

    private final QuestionDao questionDao;

    @Inject
    public QuestionService(final QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    Question save(final Question question) {
        return questionDao.insert(question);
    }

    Optional<Question> findById(final long id) {
        return Optional.ofNullable(questionDao.findById(id));
    }

    List<Question> findAll() {
        return questionDao.findAll();
    }

    void update(final Question question) {
        questionDao.update(question);
    }

    void updateCountOfAnswer(final long questionId) {
        questionDao.updateCountOfAnswer(questionId);
    }

    void deleteById(final long questionId) {
        questionDao.delete(questionId);
    }

    public void deleteAll() {
        questionDao.deleteAll();
    }
}
