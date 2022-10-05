package core.di.scanner;

import core.di.factory.BeanFactory;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class ClasspathBeanScannerTest {

    @Test
    void scanTest() {
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner();
        BeanFactory beanFactory = new BeanFactory();
        classpathBeanScanner.scan(beanFactory, "core.di.factory");
        Set<Class<?>> preInstantiatedBeans = beanFactory.getPreInstantiatedBeans();
        Set<Class<?>> expected = new HashSet<>(Arrays.asList(JdbcQuestionRepository.class, JdbcUserRepository.class, MyQnaService.class, QnaController.class));

        Assertions.assertThat(preInstantiatedBeans).isEqualTo(expected);
    }

}
