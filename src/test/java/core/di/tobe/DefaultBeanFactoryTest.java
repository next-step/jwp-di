package core.di.tobe;

import core.annotation.Repository;
import core.di.factory.example.*;
import core.di.tobe.scanner.ConfigurationBeanScanner;
import core.di.tobe.scanner.DefaultBeanScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultBeanFactoryTest {

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanFactory = DefaultBeanFactory.getInstance();
    }

    @DisplayName("Configuration bean 주입")
    @Test
    public void registerBean() {
        ConfigurationBeanScanner beanScanner = new ConfigurationBeanScanner(beanFactory, DI_DEFAULT_PACKAGE);
        beanScanner.enroll();
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
        assertThat(beanFactory.getBean(MyJdbcTemplate.class)).isNotNull();
    }

    @DisplayName("QnaController와 DI 빈 등록 성공")
    @Test
    public void di() {
        DefaultBeanScanner beanScanner2 = new DefaultBeanScanner(beanFactory, DI_DEFAULT_PACKAGE);
        beanScanner2.enroll();
        beanFactory.initialize();

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
        DefaultBeanScanner beanScanner = new DefaultBeanScanner(beanFactory, DI_DEFAULT_PACKAGE);
        beanScanner.enroll();
        beanFactory.initialize();

        Map<Class<?>, Object> beans = beanFactory.getBeans(Repository.class);
        assertThat(beans).hasSize(2);
        assertThat(beans).containsKeys(JdbcUserRepository.class, JdbcQuestionRepository.class);
    }
}