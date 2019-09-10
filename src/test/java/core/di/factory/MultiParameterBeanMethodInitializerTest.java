package core.di.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class MultiParameterBeanMethodInitializerTest extends BeanMethodInitializerTestSupport {

    private MultiParameterBeanMethodInitializer multiParameterBeanMethodInitializer;

    @BeforeEach
    void setup() {
        multiParameterBeanMethodInitializer = new MultiParameterBeanMethodInitializer(new BeanFactory());
    }

    @DisplayName("파라미터가 1개 이상 일때만 true")
    @Test
    void support() throws Exception {
        Method method = getMethod("oneParameterMethod");

        boolean support = multiParameterBeanMethodInitializer.support(method);

        assertThat(support).isTrue();
    }

    @DisplayName("파라미터가 0개면 false")
    @Test
    void support_parameter_method() throws Exception {
        Method method = getMethod("noneParameterMethod");

        boolean support = multiParameterBeanMethodInitializer.support(method);

        assertThat(support).isFalse();
    }
}