package core.di.factory.example;

import core.annotation.Inject;

public class MyAnswerService {

    private final AnswerRepository answerRepository;

    @Inject
    public MyAnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public AnswerRepository getAnswerRepository() {
        return answerRepository;
    }
}
