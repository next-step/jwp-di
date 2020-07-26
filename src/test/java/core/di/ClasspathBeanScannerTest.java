package core.di;

import core.di.factory.DefaultBeanFactory;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClasspathBeanScannerTest {

    @DisplayName("기준 package 하위에 있는 Bean들을 스캔한다.")
    @Test
    void scan() {
        /* given */
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(new DefaultBeanFactory());

        /* when */
        Set<Class<?>> preInstantiateClasses = classpathBeanScanner.scan("core.di.factory.example");

        /* then */
        assertThat(preInstantiateClasses).hasSize(4);
        assertThat(preInstantiateClasses).containsExactlyInAnyOrder(QnaController.class, MyQnaService.class, JdbcUserRepository.class, JdbcQuestionRepository.class);
    }

}
