package core.di.factory;

import core.di.scanner.ScannableAnnotaionTypes;
import core.di.scanner.ScannableAnnotaionTypesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentTypeInitializerTest {

    private ComponentTypeInitializer componentTypeInitializer;

    @BeforeEach
    void setup() {
        componentTypeInitializer = new ComponentTypeInitializer(new BeanFactory());
    }

    @DisplayName("type 이 Class 이고, 스캔대상 애노테이션을 가진 클래스이면 true")
    @ParameterizedTest(name = "[{index}] type : {0}")
    @MethodSource("supportArguments")
    void support(Class<?> type) throws Exception {
        boolean support = componentTypeInitializer.support(type);

        assertThat(support).isTrue();
    }

    private static Stream<Arguments> supportArguments() {
        return Stream.of(
                Arguments.of(ScannableAnnotaionTypesTest.ControllerClass.class),
                Arguments.of(ScannableAnnotaionTypesTest.ServiceClass.class),
                Arguments.of(ScannableAnnotaionTypesTest.RepositoryClass.class)
        );
    }

    @DisplayName("type 이 Class 이지만, 스캔대상 애노테이션이 없으면 false")
    @Test
    void support_not_scannable() throws Exception {
        Class<?> type = ScannableAnnotaionTypesTest.NoneClass.class;

        boolean support = componentTypeInitializer.support(type);

        assertThat(support).isFalse();
    }

    @DisplayName("type 이 method 이면 false")
    @Test
    void support_method_type() throws Exception {
        Method method = this.getClass().getDeclaredMethods()[0];

        boolean support = componentTypeInitializer.support(method);

        assertThat(support).isFalse();
    }
}