package core.di.factory;

import core.configuration.TestApplicationConfiguration;
import core.di.ApplicationContext;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {

    @Test
    public void di() throws Exception {
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.initialize(TestApplicationConfiguration.class);

        BeanFactory beanFactory = applicationContext.getBeanFactory();
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}
