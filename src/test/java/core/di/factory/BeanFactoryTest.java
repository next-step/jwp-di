package core.di.factory;

import core.di.factory.example.*;
import core.mvc.tobe.BeanScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        BeanScanner beanScanner = new BeanScanner("core.di.factory.example");
        beanFactory = new BeanFactory(beanScanner.getTypesAnnotatedWith());
        beanFactory.initialize();
    }

    @Test
    void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }
}
