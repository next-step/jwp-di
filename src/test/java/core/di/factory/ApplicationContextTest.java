package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.annotation.Component;
import core.annotation.web.Controller;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplicationContextTest {

    private ApplicationContext ac;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        ac = new ApplicationContext(IntegrationConfig.class);
    }

    @DisplayName("ApplicationContext의 설정파일기반 Bean 초기화 테스트")
    @Test
    void getBeanTest() {
        QnaController qnaController = ac.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());

        assertNotNull(ac.getBean(DataSource.class));

        JdbcUserRepository userRepository = ac.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = ac.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }

    @DisplayName("ApplicationContext의 초기화된 Bean들 중 특정 Annotation Bean들 가져오는 테스트")
    @Test
    void getAnnotatedWithTest() {
        Map<Class<?>, Object> scanned = ac.getBeansAnnotatedWith(Controller.class);
        Map<Class<?>, Object> notScanned = ac.getBeansAnnotatedWith(Component.class);

        assertThat(scanned.size()).isNotZero();
        assertThat(notScanned.size()).isZero();
    }
}
