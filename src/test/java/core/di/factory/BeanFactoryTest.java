package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.bean.Bean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner("core.di.factory.example");
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(configurationBeanScanner.getConfiguration());

        Collection<Bean> beanConstructors = new ArrayList<>();
        beanConstructors.addAll(configurationBeanScanner.scan());
        beanConstructors.addAll(classpathBeanScanner.scan());
        beanFactory = new BeanFactory(beanConstructors);
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

    @Test
    @DisplayName("Controller 애노테이션 있는 빈들 조회")
    void annotatedWith() {
        //when
        Collection<Object> controllers = beanFactory.annotatedWith(Controller.class);
        //then
        assertThat(controllers).hasExactlyElementsOfTypes(QnaController.class);
    }
}
