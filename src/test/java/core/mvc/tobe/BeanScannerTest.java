package core.mvc.tobe;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    private BeanScanner beanScanner;

    @BeforeEach
    void setup() {
        this.beanScanner = new BeanScanner("core.mvc.tobe");
    }

    @Test
    void getAllBeanClasses() throws Exception {
        Set<Class<?>> beanClasses = beanScanner.getAllBeanClasses();

        assertThat(beanClasses).contains(MyController.class, TestService.class, TestRepository.class);
    }

    @DisplayName("getBeanClassesWithAnnotation - 입력받은 타입의 클래스들을 반환한다.")
    @ParameterizedTest
    @MethodSource("getTestArguments")
    void getBeanClassesWithAnnotation(Class<? extends Annotation> annotationClass, Class<?> beanClass) {
        Set<Class<?>> beanClassesWithAnnotation = beanScanner.getBeanClassesWithAnnotation(annotationClass);

        assertThat(beanClassesWithAnnotation).contains(beanClass);
    }

    static Stream<Arguments> getTestArguments() {
        return Stream.of(
                Arguments.of(Controller.class, MyController.class),
                Arguments.of(Service.class, TestService.class),
                Arguments.of(Repository.class, TestRepository.class)
        );
    }

    @Service
    public static class TestService {}

    @Repository
    public static class TestRepository {}
}