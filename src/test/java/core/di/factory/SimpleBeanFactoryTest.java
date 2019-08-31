package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleBeanFactoryTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        applicationContext = new ClasspathApplicationContext("core.di.factory.example");
    }

    @DisplayName("SimpleBeanFactory 에 빈이 존재하지 않을 경우 Null 반환")
    @Test
    void getBean_NULL() {
        final NotExistBean emptyBean = applicationContext.getBean(NotExistBean.class);
        assertNull(emptyBean);

        final Object nullBean = applicationContext.getBean(null);
        assertNull(nullBean);
    }

    private static class NotExistBean {} // 테스트 용 클래스

    @Test
    public void di() {
        QnaController qnaController = applicationContext.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}
