package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {
    @DisplayName("패키지 경로를 통해 Bean을 scan 한다")
    @Test
    void scanUsingPackagePath() {
        BeanScanner beanScanner = new BeanScanner();
        beanScanner.scan("core.di.factory.example");

        QnaController qnaController = beanScanner.getBean(QnaController.class);
        assertThat(qnaController)
                .isInstanceOf(QnaController.class);
        assertThat(qnaController.getQnaService())
                .isSameAs(beanScanner.getBean(MyQnaService.class));
        assertThat(qnaController.getQnaService().getQuestionRepository())
                .isSameAs(beanScanner.getBean(QuestionRepository.class));
        assertThat(qnaController.getQnaService().getUserRepository())
                .isSameAs(beanScanner.getBean(UserRepository.class));
    }
}
