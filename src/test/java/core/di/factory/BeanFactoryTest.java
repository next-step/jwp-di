package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.mvc.tobe.ApplicationContext;
import next.config.IntegrationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    ApplicationContext ap;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        ap = new ApplicationContext(IntegrationConfig.class);
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = ap.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }
}
