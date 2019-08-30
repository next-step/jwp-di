package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.CannotDeleteException;
import next.exception.QuestionNotFoundException;
import next.model.Answer;
import next.model.Question;
import next.model.User;

import java.util.List;

@Service
public class QnaService {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @Inject
    public QnaService(final QuestionService questionService,
                      final AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    public Question save(final Question question) {
        return questionService.save(question);
    }

    public Answer save(final Answer answer) {
        final Answer savedAnswer = answerService.save(answer);
        questionService.updateCountOfAnswer(savedAnswer.getQuestionId());

        return savedAnswer;
    }

    public Question findQuestionBy(final long questionId) {
        return questionService.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }

    public Question findQuestionBy(final long questionId,
                                   final User user) {
        final Question question = findQuestionBy(questionId);
        if (!question.isSameUser(user)) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }

        return question;
    }

    public List<Question> findAllQuestion() {
        return questionService.findAll();
    }

    public void update(final long questionId,
                       final Question newQuestion,
                       final User user) {
        final Question question = findQuestionBy(questionId, user);
        question.update(newQuestion);
        questionService.update(question);
    }

    public List<Answer> findAllAnswerBy(final long questionId) {
        return answerService.findAllByQuestionId(questionId);
    }

    public void deleteQuestionBy(final long questionId,
                                 final User user) throws CannotDeleteException {
        final Question question = findQuestionBy(questionId, user);
        final List<Answer> answers = answerService.findAllByQuestionId(questionId);

        final String writer = question.getWriter();
        final boolean canDelete = answers.stream()
                .map(Answer::getWriter)
                .allMatch(writer::equals);

        if (!canDelete) {
            throw new CannotDeleteException("다른 사용자가 추가한 댓글이 존재해 삭제할 수 없습니다.");
        }

        questionService.deleteById(questionId);
    }

    public void deleteAnswerBy(final long answerId) {
        answerService.deleteById(answerId);
    }
}
