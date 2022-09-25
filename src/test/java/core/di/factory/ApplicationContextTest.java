package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        applicationContext = new ApplicationContext(IntegrationConfig.class, TestConfig.class);
        applicationContext.initialize();
    }

    @Test
    @DisplayName("자동, 수동 스캔 통합 테스트")
    void configAndClassPathIntegrationTest() {
        QnaController qnaController = applicationContext.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());

        TestA testA = applicationContext.getBean(TestA.class);

        assertNotNull(testA);
        assertNotNull(testA.getTestB());
        assertNotNull(testA.getTestD());

        TestB testB = testA.getTestB();
        assertNotNull(testB.getTestC());

        MyJdbcTemplate myJdbcTemplate = applicationContext.getBean(MyJdbcTemplate.class);
        assertNotNull(myJdbcTemplate);
        assertNotNull(myJdbcTemplate.getDataSource());
    }
}