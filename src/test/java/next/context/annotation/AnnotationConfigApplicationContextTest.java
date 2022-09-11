package next.context.annotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.BeanFactory;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.UserRepository;
import core.mvc.tobe.ClassPathBeanScanner;
import core.mvc.tobe.ConfigurationBeanScanner;
import javax.sql.DataSource;
import next.config.MyConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnnotationConfigApplicationContextTest {

    private AnnotationConfigApplicationContext annotationConfigApplicationContext;

    @BeforeEach
    void setUp() {
        this.annotationConfigApplicationContext
            = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }

    @DisplayName("Configuration 클래스의 ComponentScan 값의 basePackages 하위 빈들이 추가된다.")
    @Test
    void registerTest() {
        BeanFactory beanFactory = this.annotationConfigApplicationContext.getBeanFactory();

        assertNotNull(beanFactory.getBean(DataSource.class));

        MyQnaService qnaService = beanFactory.getBean(MyQnaService.class);
        assertNotNull(qnaService);

        UserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
    }

}
