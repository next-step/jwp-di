package core.di.factory;

import core.annotation.Repository;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        BeanScanner beanScanner = new BeanScanner(DI_DEFAULT_PACKAGE);
        this.beanFactory = BeanFactory.initialize(beanScanner.enroll());
    }

    @DisplayName("QnaController와 DI 빈 등록 성공")
    @Test
    public void di() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @DisplayName("Repository 어노테이션 붙은 빈을 모두 리턴받는다")
    @Test
    public void getBeans() {
        Map<Class<?>, Object> beans = beanFactory.getBeans(Repository.class);
        assertThat(beans).hasSize(2);
        assertThat(beans).containsKeys(JdbcUserRepository.class, JdbcQuestionRepository.class);
    }
}