package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.CannotDeleteException;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;

import java.util.List;

@Service
public class QnaService {

    @Inject
    private QuestionDao questionDao;

    @Inject
    private AnswerDao answerDao;

    public Question findById(long questionId) {
        return questionDao.findById(questionId);
    }

    public List<Question> findAll() {
        return questionDao.findAll();
    }

    public Question insert(Question question) {
        return questionDao.insert(question);
    }

    public Answer insert(Answer answer) {
        return answerDao.insert(answer);
    }

    public void update(long questionId, Question updatedQuestion) {
        final Question question = questionDao.findById(questionId);
        question.update(updatedQuestion);
        questionDao.update(question);
    }

    public void updateCountOfAnswer(long questionId) {
        questionDao.updateCountOfAnswer(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return answerDao.findAllByQuestionId(questionId);
    }

    public void deleteQuestion(long questionId, User user) throws CannotDeleteException {
        Question question = questionDao.findById(questionId);
        if (question == null) {
            throw new CannotDeleteException("존재하지 않는 질문입니다.");
        }

        if (!question.isSameUser(user)) {
            throw new CannotDeleteException("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
        }

        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        if (answers.isEmpty()) {
            questionDao.delete(questionId);
            return;
        }

        boolean canDelete = true;
        for (Answer answer : answers) {
            String writer = question.getWriter();
            if (!writer.equals(answer.getWriter())) {
                canDelete = false;
                break;
            }
        }

        if (!canDelete) {
            throw new CannotDeleteException("다른 사용자가 추가한 댓글이 존재해 삭제할 수 없습니다.");
        }

        questionDao.delete(questionId);
    }

    public void delete(long answerId) {
        answerDao.delete(answerId);
    }
}
