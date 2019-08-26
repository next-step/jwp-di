package next.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.annotation.Inject;
import core.annotation.Service;
import next.CannotDeleteException;
import next.dto.QuestionCreateDto;
import next.dto.QuestionUpdateDto;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.repository.AnswerRepository;
import next.repository.QuestionRepository;

import java.util.List;

@Service
public class QnaService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;

    @Inject
    public QnaService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Question findById(long questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }

    public List<Question> findAllQuestion() {
        return questionRepository.findAll();
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

    public void insertQuestion(User user, QuestionCreateDto questionCreateDto) {
        Question question = new Question(user.getUserId(),
                questionCreateDto.getTitle(),
                questionCreateDto.getContents());
        questionRepository.insert(question);
    }

    public Question updateQuestion(long questionId, User user) {
        Question question = questionRepository.findById(questionId);
        confirmIdentification(question, user);
        return question;
    }

    public void updateQuestion(long questionId, QuestionUpdateDto updateDto, User user) {
        Question question = questionRepository.findById(questionId);
        confirmIdentification(question, user);

        Question updateQuestion = objectMapper.convertValue(updateDto, Question.class);
        question.update(updateQuestion);

        questionRepository.update(question);
    }

    private void confirmIdentification(Question question, User user) {
        if (!question.isSameUser(user)) {
            throw new DataAccessException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }
    }

    public Answer insertAnswer(Answer answer) {
        Answer savedAnswer = answerRepository.insert(answer);
        questionRepository.updateCountOfAnswer(savedAnswer.getQuestionId());
        return savedAnswer;
    }

    public void deleteAnswer(User user, Long answerId) throws CannotDeleteException {
        Answer answer = answerRepository.findById(answerId);
        if (!answer.isSameUser(user)) {
            throw new CannotDeleteException("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
        }
        answerRepository.delete(answerId);
    }
}