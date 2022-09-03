package next.dao;

import core.jdbc.ConnectionManager;
import core.mvc.tobe.AnnotationHandlerMapping;
import next.ApplicationContext;
import next.config.MyConfiguration;
import next.model.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import support.test.DBInitializer;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerDaoTest {
    private static final Logger log = LoggerFactory.getLogger(AnswerDaoTest.class);

    private ApplicationContext applicationContext;

    @BeforeEach
    public void setup() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());

        applicationContext = new ApplicationContext(MyConfiguration.class);
        applicationContext.initialize();
    }

    @Test
    public void addAnswer() throws Exception {
        long questionId = 1L;
        Answer expected = new Answer("javajigi", "answer contents", questionId);
        AnswerDao dut = applicationContext.getBean(AnswerDao.class);
        Answer answer = dut.insert(expected);
        log.debug("Answer : {}", answer);
        assertThat(answer).isNotNull();
    }
}
