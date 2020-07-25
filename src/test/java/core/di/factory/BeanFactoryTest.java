package core.di.factory;

import core.di.BeanScanner;
import core.di.ApplicationContext;
import core.di.ConfigurationBeanScanner;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.exception.CircularReferenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeanFactoryTest {

    @DisplayName("BasePackage 를 기준으로 BeanFactory 정상 동작")
    @Test
    public void di() {
        /* given */
        String basePackage = "core.di.factory.example";
        BeanFactory beanFactory = createBeanFactory(basePackage);
        beanFactory.initialize();

        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @DisplayName("Bean 들이 순환참조를 하면 Exception")
    @Test
    public void circularReferenceException() {
        /* given */
        String basePackage = "core.di.factory.illegal.circular";
        BeanFactory beanFactory = createBeanFactory(basePackage);

        /* when */ /* then */
        assertThrows(CircularReferenceException.class, beanFactory::initialize);
    }

    private BeanFactory createBeanFactory(String basePackage) {
        ApplicationContext applicationContext = new ApplicationContext(new BeanScanner(basePackage), new ConfigurationBeanScanner(basePackage));
        return new BeanFactory(applicationContext);
    }

}
