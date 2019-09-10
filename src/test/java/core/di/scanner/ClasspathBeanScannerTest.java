package core.di.scanner;

import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import core.mvc.tobe.MyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ClasspathBeanScannerTest {

    private ClasspathBeanScanner beanScanner;

    @BeforeEach
    void setup() {
        beanScanner = new ClasspathBeanScanner(new BeanFactory());
        beanScanner.doScan("core.mvc.tobe", "core.di.scanner");
    }

    @DisplayName("getBeanClassesWithAnnotation - 입력받은 타입의 클래스들을 반환한다.")
    @ParameterizedTest(name = "[{index}] annotation : {0}, beanClass : {1}")
    @MethodSource("getTestArguments")
    void getBeanClassesWithAnnotation(Class<? extends Annotation> annotationClass, Class<?> beanClass) {
        Set<Class<?>> beanClassesWithAnnotation = beanScanner.getBeanClassesWithAnnotation(annotationClass);

        assertThat(beanClassesWithAnnotation).contains(beanClass);
    }

    static Stream<Arguments> getTestArguments() {
        return Stream.of(
                Arguments.of(Controller.class, MyController.class),
                Arguments.of(Service.class, TestService.class),
                Arguments.of(Repository.class, TestRepository.class),
                Arguments.of(Configuration.class, TestConfig.class)
        );
    }

    @Configuration
    public static class TestConfig {}

    @Service
    public static class TestService {}

    @Repository
    public static class TestRepository {}

}