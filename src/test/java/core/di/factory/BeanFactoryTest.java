package core.di.factory;

import com.google.common.collect.Sets;
import core.di.factory.definition.BeanDefinitionRegistry;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.scanner.ClassPathBeanScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        BeanDefinitionRegistry beanDefinitions = new BeanDefinitionRegistry();
        ClassPathBeanScanner cbds = new ClassPathBeanScanner(beanDefinitions);
        cbds.doScan(ExampleConfig.class);
        beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.register();
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
