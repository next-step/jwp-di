package next.dao;

import static org.assertj.core.api.Assertions.assertThat;

import core.di.factory.ApplicationContext;
import javax.sql.DataSource;
import next.model.Answer;
import next.support.config.MyWebAppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class AnswerDaoTest {

    private static final Logger log = LoggerFactory.getLogger(AnswerDaoTest.class);

    private AnswerDao answerDao;

    @BeforeEach
    public void setup() {
        ApplicationContext ac = new ApplicationContext(MyWebAppConfiguration.class);
        initializeDB(ac);
        this.answerDao = ac.getBean(AnswerDao.class);
    }

    private void initializeDB(ApplicationContext ac) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ac.getBean(DataSource.class));
    }

    @Test
    public void addAnswer() throws Exception {
        long questionId = 1L;
        Answer expected = new Answer("javajigi", "answer contents", questionId);
        Answer answer = answerDao.insert(expected);
        log.debug("Answer : {}", answer);
        assertThat(answer).isNotNull();
    }
}
