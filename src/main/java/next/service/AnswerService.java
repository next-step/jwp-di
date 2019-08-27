package next.service;

import core.annotation.Inject;
import next.dao.AnswerDao;
import next.model.Answer;

import java.util.List;
import java.util.Optional;

public class AnswerService {

    private final AnswerDao answerDao;

    @Inject
    public AnswerService(final AnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    Answer save(final Answer answer) {
        return answerDao.insert(answer);
    }

    Optional<Answer> findById(final long id) {
        return Optional.ofNullable(answerDao.findById(id));
    }

    List<Answer> findAllByQuestionId(final long questionId) {
        return answerDao.findAllByQuestionId(questionId);
    }

    void deleteById(final long answerId) {
        answerDao.delete(answerId);
    }
}
