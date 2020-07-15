package core.di;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @DisplayName("기준 package 하위에 있는 Bean들을 스캔한다.")
    @Test
    void scan() {
        /* given */
        BeanScanner beanScanner = new BeanScanner("core.di.factory.example");

        /* when */
        Set<Class<?>> preInstantiateClazz = beanScanner.scan();

        /* then */
        assertThat(preInstantiateClazz).hasSize(4);
        assertThat(preInstantiateClazz).containsExactlyInAnyOrder(QnaController.class, MyQnaService.class, JdbcUserRepository.class, JdbcQuestionRepository.class);
    }

}
