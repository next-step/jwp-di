package next.dao;

import static org.assertj.core.api.Assertions.assertThat;

import next.model.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AnswerDaoTest extends TestEnvironment {
    private static final Logger log = LoggerFactory.getLogger(AnswerDaoTest.class);

    private AnswerDao answerDao;

    @BeforeEach
    public void setup() {
        this.setUpConfig();

        this.answerDao = this.beanFactory.getBean(AnswerDao.class);
    }

    @Test
    void addAnswer() throws Exception {
        long questionId = 1L;
        Answer expected = new Answer("javajigi", "answer contents", questionId);
        Answer answer = answerDao.insert(expected);
        log.debug("Answer : {}", answer);
        assertThat(answer).isNotNull();
    }
}
