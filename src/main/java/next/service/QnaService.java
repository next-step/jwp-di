package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.CannotDeleteException;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.repository.JdbcAnswerRepository;
import next.repository.JdbcQuestionRepository;

import java.util.List;

@Service
public class QnaService {

    private JdbcQuestionRepository questionRepository;
    private JdbcAnswerRepository answerRepository;

    @Inject
    public QnaService(JdbcQuestionRepository questionRepository, JdbcAnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Question findById(long questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }

    public void deleteQuestion(long questionId, User user) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId);
        if (question == null) {
            throw new CannotDeleteException("존재하지 않는 질문입니다.");
        }

        if (!question.isSameUser(user)) {
            throw new CannotDeleteException("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
        }

        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
        if (answers.isEmpty()) {
            questionRepository.delete(questionId);
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

        questionRepository.delete(questionId);
    }
}
