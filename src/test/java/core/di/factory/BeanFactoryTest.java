package core.di.factory;

import core.di.factory.example.BoardService;
import core.di.factory.example.MockBoardRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private DefaultBeanFactory beanFactory;
    private BeanScanner beanScanner;

    @BeforeEach
    public void setup() {
        beanFactory = new DefaultBeanFactory();
        beanScanner = new BeanScanner(beanFactory);

        beanScanner.scan("core.di.factory.example");
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
    public void qualifierTest() {
        BoardService boardService = beanFactory.getBean(BoardService.class);

        Assertions.assertThat(boardService.getBoardRepository()).isInstanceOf(MockBoardRepository.class);
    }
}
