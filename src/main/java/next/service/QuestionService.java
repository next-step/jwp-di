package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.model.Question;
import next.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Inject
    public QuestionService(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    Question save(final Question question) {
        return questionRepository.insert(question);
    }

    Optional<Question> findById(final long id) {
        return Optional.ofNullable(questionRepository.findById(id));
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    void update(final Question question) {
        questionRepository.update(question);
    }

    void updateCountOfAnswer(final long questionId) {
        questionRepository.updateCountOfAnswer(questionId);
    }

    void deleteById(final long questionId) {
        questionRepository.delete(questionId);
    }

    public void deleteAll() {
        questionRepository.deleteAll();
    }
}
