package core.di.factory;

import core.di.AnnotatedBeanDefinitionReader;
import core.di.BeanDefinitionRegistry;
import core.di.ClassPathBeanDefinitionScanner;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationContextTest {

    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        BeanDefinitionRegistry beanDefinitionRegistry = new BeanDefinitionRegistry();
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        reader.register(Set.of(TestConfig.class, IntegrationConfig.class));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        scanner.scan("core.di.factory.example");
        beanFactory = new BeanFactory(beanDefinitionRegistry);
        beanFactory.initialize();
    }

    @Test
    @DisplayName("자동, 수동 스캔 통합 테스트")
    void configAndClassPathIntegrationTest() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());

        TestA testA = beanFactory.getBean(TestA.class);

        assertNotNull(testA);
        assertNotNull(testA.getTestB());
        assertNotNull(testA.getTestD());

        TestB testB = testA.getTestB();
        assertNotNull(testB.getTestC());

        MyJdbcTemplate myJdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(myJdbcTemplate);
        assertNotNull(myJdbcTemplate.getDataSource());
    }
}