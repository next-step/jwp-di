package core.di.factory;

import core.annotation.*;
import core.annotation.web.Controller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("빈 타입")
class BeanTypeTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("어노테이션에 따라사 올바른 빈 타입을 가져오는지")
    void of(Class<? extends Annotation> annotation, BeanType beanType) {
        assertThat(BeanType.of(annotation)).isEqualTo(beanType);
    }

    private static Stream<Arguments> of() {
        return Stream.of(
                Arguments.of(Controller.class, BeanType.CONTROLLER),
                Arguments.of(Service.class, BeanType.SERVICE),
                Arguments.of(Repository.class, BeanType.REPOSITORY),
                Arguments.of(Component.class, BeanType.COMPONENT),
                Arguments.of(Configuration.class, BeanType.CONFIGURATION),
                Arguments.of(Bean.class, BeanType.BEAN)
        );
    }
}
