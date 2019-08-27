package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleBeanFactoryTest {
    private SimpleBeanFactory simpleBeanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        simpleBeanFactory = new SimpleBeanFactory("core.di.factory.example");
        simpleBeanFactory.initialize();
    }

    @DisplayName("SimpleBeanFactory 에 빈이 존재하지 않을 경우 Null 반환")
    @Test
    void getBean_NULL() {
        final NotExistBean emptyBean = simpleBeanFactory.getBean(NotExistBean.class);
        assertNull(emptyBean);

        final Object nullBean = simpleBeanFactory.getBean(null);
        assertNull(nullBean);
    }

    private static class NotExistBean {} // 테스트 용 클래스

    @Test
    public void di() {
        QnaController qnaController = simpleBeanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

}
