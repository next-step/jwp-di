package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.di.factory.example.MyConfiguration;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;

public class BeanFactoryTest {
    private ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner;
    private ConfigurationBeanDefinitionReader configurationBeanDefinitionReader;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanFactory = new BeanFactory();
        classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanFactory);
        classPathBeanDefinitionScanner.loadBeanDefinitions("core.di.factory.example");
        configurationBeanDefinitionReader = new ConfigurationBeanDefinitionReader(beanFactory);
        configurationBeanDefinitionReader.loadBeanDefinitions(MyConfiguration.class);
        beanFactory.initialize();
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }


}
