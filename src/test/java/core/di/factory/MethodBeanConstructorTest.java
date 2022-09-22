package core.di.factory;

import core.annotation.Bean;

import core.di.factory.constructor.MethodBeanConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@DisplayName("메서드 빈 생성")
class MethodBeanConstructorTest {

    @DisplayName("빈 생성")
    @Test
    void instance() {
        assertThatNoException().isThrownBy(
                () -> new MethodBeanConstructor(testBeanMethod()));
    }

    @DisplayName("메서드 생략시 빈 생성 실패")
    @Test
    void fail_instance_without_method() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new MethodBeanConstructor(null));
    }

    @DisplayName("@Bean 어노테이션 필수")
    @Test
    void fail_instance_without_annotation() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new MethodBeanConstructor(withOutBeanAnnotationMethod()));
    }

    private Method testBeanMethod() throws NoSuchMethodException {
        return MethodBeanConstructorTest.class.getDeclaredMethod("testBean", TestParameter.class);
    }

    @Bean
    public TestBean testBean(TestParameter parameter) {
        return new TestBean();
    }

    private Method withOutBeanAnnotationMethod() throws NoSuchMethodException {
        return MethodBeanConstructorTest.class.getDeclaredMethod("withOutBean", TestParameter.class);
    }

    public TestBean withOutBean(TestParameter parameter) {
        return new TestBean();
    }

    private static class TestParameter {
    }

    private static class TestBean {
    }
}
