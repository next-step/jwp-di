package core.di;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.exception.CircularReferenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextTest {

    @DisplayName("BasePackage 를 기준으로 BeanFactory 정상 동작")
    @Test
    public void di() {
        /* given */
        ApplicationContext applicationContext = new ApplicationContext("core.di.factory.example");

        QnaController qnaController = applicationContext.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @DisplayName("Bean 들이 순환참조를 하면 Exception")
    @Test
    public void circularReferenceException() {
        /* given */
        String basePackage = "core.di.factory.illegal.circular";

        /* when */ /* then */
        assertThrows(CircularReferenceException.class, () -> new ApplicationContext(basePackage));
    }

    @DisplayName("BeanScanner 와 ConfigurationBeanScanner 를 통해 기준 package 하위에 있는 모든 Bean들을 스캔한다.")
    @Test
    void scan() {
        /* given */
        ApplicationContext applicationContext = new ApplicationContext("core.di.factory.example");

        /* when */
        List<Class<?>> beanClasses = applicationContext.getBeanClasses();

        /* then */
        assertThat(beanClasses).hasSize(6);
        assertThat(beanClasses).containsExactlyInAnyOrder(JdbcQuestionRepository.class, MyJdbcTemplate.class,
                QnaController.class, JdbcUserRepository.class, MyQnaService.class, DataSource.class);
    }

    @DisplayName("Bean 스캔 시 중복되는 Bean 후보가 있다면 Exception")
    @Test
    void scan_exception() {
        /* given */
        String basePackage = "core.di.factory.illegal.configuration";

        /* when */ /* then */
        assertThrows(IllegalStateException.class, () -> new ApplicationContext(basePackage));
    }

}
