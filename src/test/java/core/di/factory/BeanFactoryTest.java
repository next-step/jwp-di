package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    void setup() {
        beanFactory = BeanFactory.from(BeanScanner.packages("core.di.factory.example"));
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
    void annotatedWith() {
        //when
        Map<Class<?>, Object> controllers = beanFactory.annotatedWith(Controller.class);
        //then
        assertThat(controllers).hasEntrySatisfying(
                QnaController.class, value -> assertThat(value).isInstanceOf(QnaController.class));
    }
}
