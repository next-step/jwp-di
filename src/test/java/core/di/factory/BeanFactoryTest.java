package core.di.factory;

import core.di.factory.example.*;
import core.di.scanner.ClasspathBeanScanner;
import core.di.scanner.ConfigurationBeanScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(IntegrationConfig.class);
        beanFactory.initialize();

        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(configurationBeanScanner.getBasePackages());
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getQuestionRepository());
        UserRepository userRepository = qnaService.getUserRepository();
        assertThat(userRepository).isInstanceOf(JdbcUserRepository.class);
        assertThat(((JdbcUserRepository) userRepository).getMyJdbcTemplate()).isInstanceOf(MyJdbcTemplate.class);
    }
}
