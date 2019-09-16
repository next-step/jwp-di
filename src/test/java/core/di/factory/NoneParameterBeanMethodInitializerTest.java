package core.di.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class NoneParameterBeanMethodInitializerTest extends BeanMethodInitializerTestSupport {

    private NoneParameterBeanMethodInitializer noneParameterBeanMethodInitializer;

    @BeforeEach
    void setup() {
        noneParameterBeanMethodInitializer = new NoneParameterBeanMethodInitializer();
    }

    @DisplayName("파라미터가 0개일때만 true")
    @Test
    void support() throws Exception {
        Method method = getMethod("noneParameterMethod");

        boolean support = noneParameterBeanMethodInitializer.support(method);

        assertThat(support).isTrue();
    }

    @DisplayName("파라미터가 0개가 아니면 false")
    @Test
    void support_parameter_method() throws Exception {
        Method method = getMethod("oneParameterMethod");

        boolean support = noneParameterBeanMethodInitializer.support(method);

        assertThat(support).isFalse();
    }

}