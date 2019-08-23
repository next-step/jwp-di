package next.repository;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.jdbc.ConnectionManager;
import next.model.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcAnswerRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(JdbcAnswerRepositoryTest.class);

    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        beanFactory = new BeanFactory();
        BeanScanner beanScanner = new BeanScanner(beanFactory, "next.repository");
        beanScanner.scan();

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    @Test
    public void addAnswer() throws Exception {
        long questionId = 1L;
        Answer expected = new Answer("javajigi", "answer contents", questionId);
        JdbcAnswerRepository repository = beanFactory.getBean(JdbcAnswerRepository.class);
        Answer answer = repository.insert(expected);
        log.debug("Answer : {}", answer);
        assertThat(answer).isNotNull();
    }
}