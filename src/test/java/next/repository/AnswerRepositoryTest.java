package next.repository;

import next.model.Answer;
import next.repository.impl.JdbcAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(AnswerRepositoryTest.class);

    private AnswerRepository repository = new JdbcAnswerRepository();

    @BeforeEach
    void setUp() {
        AutoConfigureTestDatabase.setup();
    }

    @Test
    public void addAnswer() {
        long questionId = 1L;
        Answer expected = new Answer("javajigi", "answer contents", questionId);
        Answer answer = repository.insert(expected);

        log.debug("Answer : {}", answer);
        assertThat(answer).isNotNull();
    }
}