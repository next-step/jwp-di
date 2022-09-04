package core.di;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.BeanFactory;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BeanContextTest {

    @DisplayName("BeanFactory를 주입받아 BeanFactory에 인스턴스를 등록한다")
    @Test
    void initialize() {
        // given
        final BeanFactory beanFactory = new BeanFactory();
        final BeanContext beanContext = new BeanContext(beanFactory, "core.di.factory.example");

        // when
        beanContext.initialize();

        // then
        final QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}
