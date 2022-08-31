package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        BeanScanner beanScanner = new BeanScanner(List.of(Controller.class, Service.class, Repository.class));
        beanFactory = new BeanFactory(beanScanner.scan("core.di.factory.example"));
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
    void getBeanWithAnnotation() {
        Set<Object> actual = beanFactory.getBeansWithAnnotation(Controller.class);

        QnaController concludedBean = beanFactory.getBean(QnaController.class);
        assertThat(actual).containsExactly(concludedBean);
    }
}
