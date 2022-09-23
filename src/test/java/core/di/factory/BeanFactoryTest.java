package core.di.factory;

import core.di.AnnotatedBeanDefinitionReader;
import core.di.BeanDefinitionRegistry;
import core.di.ClassPathBeanDefinitionScanner;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        BeanDefinitionRegistry beanDefinitionRegistry = new BeanDefinitionRegistry();
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        reader.register(Set.of(TestConfig.class));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        scanner.scan("core.di.factory.example");
        beanFactory = new BeanFactory(beanDefinitionRegistry);
        beanFactory.initialize();
    }

    @Test
    @DisplayName("자동 스캔으로 주입된 빈의 의존관계 주입 테스트")
    void classPathBeanDI() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    @DisplayName("수동 스캔으로 주입된 빈의 의존관계 주입 테스트")
    void configurationBeanDI() throws Exception {
        TestA testA = beanFactory.getBean(TestA.class);

        assertNotNull(testA);
        assertNotNull(testA.getTestB());
        assertNotNull(testA.getTestD());

        TestB testB = testA.getTestB();
        assertNotNull(testB.getTestC());
    }
}
