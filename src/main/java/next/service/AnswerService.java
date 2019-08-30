package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.model.Answer;
import next.repository.AnswerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Inject
    public AnswerService(final AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    Answer save(final Answer answer) {
        return answerRepository.insert(answer);
    }

    Optional<Answer> findById(final long id) {
        return Optional.ofNullable(answerRepository.findById(id));
    }

    List<Answer> findAllByQuestionId(final long questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }

    void deleteById(final long answerId) {
        answerRepository.delete(answerId);
    }
}
