package core.di.factory.example;

import core.annotation.Inject;

public class MockInjectedController {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Inject
    public MockInjectedController(final UserRepository userRepository, final QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @Inject
    public MockInjectedController() {
        this.userRepository = new JdbcUserRepository();
        this.questionRepository = new JdbcQuestionRepository();
    }
}
