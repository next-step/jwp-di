package core.di.factory;

import core.di.beans.definition.reader.AnnotatedBeanDefinitionReader;
import core.di.beans.definition.reader.ClasspathBeanDefinitionReader;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(BeanFactoryTest.class);

    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanFactory = new BeanFactory();
        AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        annotatedBeanDefinitionReader.read(ExampleConfig.class);

        ClasspathBeanDefinitionReader classpathBeanDefinitionReader = new ClasspathBeanDefinitionReader(beanFactory);
        classpathBeanDefinitionReader.doScan("core.di.factory.example");
        beanFactory.instantiateBeans();
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

    @Test
    void getControllers() {
        Map<Class<?>, Object> controllers = beanFactory.getControllers();

        assertThat(controllers).isNotNull();
        assertThat(controllers).hasSize(1);
    }
}
