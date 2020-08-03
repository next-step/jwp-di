package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @Test
    void test_scanBean() {
        // given
        BeanScanner beanScanner = new BeanScanner(Controller.class, Service.class, Repository.class);
        // when
        Set<Class<?>> preInstantiateBeans = beanScanner.scan("core.di.factory.example");
        // then
        assertThat(preInstantiateBeans)
                .containsExactlyInAnyOrder(
                        JdbcUserRepository.class,
                        JdbcQuestionRepository.class,
                        MyQnaService.class,
                        QnaController.class);
    }


}