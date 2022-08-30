package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.constructor.BeanConstructor;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    void setup() {
        ConfigurationBeanScanner configurationBeanScanner = ConfigurationBeanScanner.packages("core.di.factory.example");
        ClasspathBeanScanner classpathBeanScanner = ClasspathBeanScanner.from(configurationBeanScanner.configurations());

        Collection<BeanConstructor> beanConstructors = new ArrayList<>();
        beanConstructors.addAll(configurationBeanScanner.scan());
        beanConstructors.addAll(classpathBeanScanner.scan());
        beanFactory = BeanFactory.from(beanConstructors);
        beanFactory.initialize();
    }

    @Test
    void di() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    @DisplayName("Controller 애노테이션 있는 빈들 조회")
    void beansAnnotatedWith() {
        //when
        Collection<Object> controllers = beanFactory.beansAnnotatedWith(Controller.class);
        //then
        assertThat(controllers).hasExactlyElementsOfTypes(QnaController.class);
    }
}
