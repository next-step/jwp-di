package core.di;

import core.annotation.web.Controller;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationContextTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);

    private ApplicationContext applicationContext;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        applicationContext = new ApplicationContext(IntegrationConfig.class);
    }

    @DisplayName("Configuration bean 주입")
    @Test
    public void registerBean() {

        log.debug("{}", applicationContext.getBean(DataSource.class));

        assertThat(applicationContext.getBean(DataSource.class)).isNotNull();
        assertThat(applicationContext.getBean(MyJdbcTemplate.class)).isNotNull();
    }

    @DisplayName("QnaController 와 DI 빈 등록 성공")
    @Test
    public void di() {
        QnaController qnaController = applicationContext.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @DisplayName("Controller 어노테이션 붙은 빈을 모두 리턴받는다")
    @Test
    public void getBeans() {
        Map<Class<?>, Object> beans = applicationContext.getBeans(Controller.class);
        assertThat(beans).hasSize(1);
        assertThat(beans).containsKeys(QnaController.class);
    }
}