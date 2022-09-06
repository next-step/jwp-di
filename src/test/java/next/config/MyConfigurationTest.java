package next.config;

import core.di.AnnotatedBeanDefinitionReader;
import core.di.factory.DefaultListableBeanFactory;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.jdbc.JdbcTemplate;
import next.controller.ApiQnaController;
import next.controller.ApiUserController;
import next.controller.HomeController;
import next.controller.UserController;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MyConfigurationTest {

    @Test
    void scan() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader scanner = new AnnotatedBeanDefinitionReader(beanFactory);
        scanner.register(MyConfiguration.class);
        beanFactory.preInstantiateSingletons();

        Set<Class<?>> beanClasses = beanFactory.getBeanClasses();

        assertThat(beanClasses).contains(
            // @Configuration 으로 등록한 Bean
            MyConfiguration.class,
            DataSource.class,
            JdbcTemplate.class,

            // next 및 하위 패키지
            ApiQnaController.class,
            ApiUserController.class,
            HomeController.class,
            QnaController.class,
            UserController.class,

            // core 및 하위 패키지
            JdbcQuestionRepository.class,
            JdbcUserRepository.class,
            MyQnaService.class,
            QnaController.class
        );
    }
}
