package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.mvc.tobe.ClassPathBeanScanner;
import core.mvc.tobe.ConfigurationBeanScanner;
import next.config.MyConfiguration;
import next.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanFactoryTest {
    private AnnotationConfigApplicationContext annotationConfigApplicationContext;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        BeanFactory beanFactory = new BeanFactory();
        ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner(beanFactory);
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        this.annotationConfigApplicationContext = new AnnotationConfigApplicationContext(
            beanFactory, classPathBeanScanner, configurationBeanScanner
        );
        this.beanFactory = this.annotationConfigApplicationContext.getBeanFactory();
    }

    @Test
    void di() {
        this.annotationConfigApplicationContext.register(MyConfiguration.class);
        this.annotationConfigApplicationContext.scan("core.di.factory.example");

        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}
