package core.di.factory;

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
        BeanScanner beanScanner = new BeanScanner();
        // when
        Set<Class<?>> preInstantiateBeans = beanScanner.scanBean("core.di.factory.example");
        // then
        assertThat(preInstantiateBeans)
                .containsExactlyInAnyOrder(
                        JdbcUserRepository.class,
                        JdbcQuestionRepository.class,
                        MyQnaService.class,
                        QnaController.class);
    }


}