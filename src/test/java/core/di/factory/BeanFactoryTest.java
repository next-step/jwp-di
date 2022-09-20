package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {

    @BeforeAll
    @SuppressWarnings("unchecked")
    public static void init() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.getInstance().initialize("core.di.factory");
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = BeanFactory.getInstance().getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }
}
