package core.di.support;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.Map;

class BeanScannerTest {

    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new BeanScanner("next");
    }

    @DisplayName("Annotation이 된 빈들을 가져온다.")
    @ParameterizedTest
    @ValueSource(classes = {Controller.class, Service.class, Repository.class})
    void getBeansOfAnnotatedBy(final Class<? extends Annotation> annotation) {
        // when
        Map<Class<?>, Object> beans = beanScanner.getBeansOfAnnotatedBy(annotation);

        // then
        beans.keySet()
                .stream()
                .map(clazz -> clazz.isAnnotationPresent(annotation))
                .map(Assertions::assertThat)
                .forEach(AbstractBooleanAssert::isTrue);
    }
}