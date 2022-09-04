package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @Test
    void scan() {
        BeanScanner beanScanner = new BeanScanner(List.of(Controller.class, Service.class, Repository.class));

        Set<Class<?>> actual = beanScanner.scan("core.di.factory.example");
        assertThat(actual).containsExactlyInAnyOrder(
                QnaController.class,
                MyQnaService.class,
                JdbcQuestionRepository.class,
                JdbcUserRepository.class
        );
    }
}
