package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.context.AnnotationConfigApplicationContext;
import core.di.context.ApplicationContext;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultBeanFactoryTest {

    private ApplicationContext ctx;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        ctx = new AnnotationConfigApplicationContext(DummyConfig.class);
    }

    @Configuration
    @ComponentScan(basePackages = "core.di.factory.example")
    public static class DummyConfig {
        // no-op
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = ctx.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}