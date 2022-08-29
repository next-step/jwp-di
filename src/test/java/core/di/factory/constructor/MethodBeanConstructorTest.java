package core.di.factory.constructor;

import core.annotation.Bean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("메서드 빈 생성")
class MethodBeanConstructorTest {

    @Test
    @DisplayName("빈 메서드로 생성")
    void instance() {
        assertThatNoException().isThrownBy(() -> MethodBeanConstructor.from(testBeanMethod()));
    }

    @Test
    @DisplayName("메서드는 필수")
    void instance_null_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> MethodBeanConstructor.from(null));
    }

    @Test
    @DisplayName("메서드에 @Bean 애노테이션은 필수")
    void instance_withoutBeanAnnotation_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> MethodBeanConstructor.from(withOutBeanMethod()));
    }

    @Test
    @DisplayName("반환되는 타입")
    void type() throws NoSuchMethodException {
        //given
        MethodBeanConstructor testBeanMethodConstructor = MethodBeanConstructor.from(testBeanMethod());
        //when, then
        assertThat(testBeanMethodConstructor.type()).isEqualTo(TestBean.class);
    }

    @Test
    @DisplayName("파라미터 타입은 속한 클래스와 메소드 인자들")
    void parameterTypes() throws NoSuchMethodException {
        //given
        MethodBeanConstructor testBeanMethodConstructor = MethodBeanConstructor.from(testBeanMethod());
        //when, then
        assertThat(testBeanMethodConstructor.parameterTypes()).containsExactly(MethodBeanConstructorTest.class, TestParameter.class);
    }

    @Test
    @DisplayName("메소드를 포함한 객체와 인자로 생성")
    void instantiate() throws NoSuchMethodException {
        //given
        MethodBeanConstructor testBeanMethodConstructor = MethodBeanConstructor.from(testBeanMethod());
        //when, then
        assertThat(testBeanMethodConstructor.instantiate(List.of(new MethodBeanConstructorTest(), new TestParameter())))
                .isInstanceOf(TestBean.class);
    }

    private Method testBeanMethod() throws NoSuchMethodException {
        return MethodBeanConstructorTest.class.getDeclaredMethod("testBean", TestParameter.class);
    }

    private Method withOutBeanMethod() throws NoSuchMethodException {
        return MethodBeanConstructorTest.class.getDeclaredMethod("withOutBean", TestParameter.class);
    }

    @Bean
    public TestBean testBean(TestParameter parameter) {
        return new TestBean();
    }

    public TestBean withOutBean(TestParameter parameter) {
        return new TestBean();
    }

    private static class TestBean {

    }

    private static class TestParameter {

    }
}
