package core.di.factory.constructor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("클래스 빈 생성")
class ClassBeanConstructorTest {

    @Test
    @DisplayName("클래스로 생성")
    void instance() {
        assertThatNoException().isThrownBy(() -> ClassBeanConstructor.from(TestClass.class));
    }

    @Test
    @DisplayName("클래스는 필수")
    void instance_null_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ClassBeanConstructor.from(null));
    }

    @Test
    @DisplayName("주어진 타입 반환")
    void type() {
        //given
        Class<TestClass> clazz = TestClass.class;
        //when, then
        assertThat(ClassBeanConstructor.from(clazz).type()).isEqualTo(clazz);
    }

    @Test
    @DisplayName("인스턴스화 가능 여부")
    void isNotInstanced() {
        assertAll(
                () -> assertThat(ClassBeanConstructor.from(TestInterface.class).isNotInstanced()).isTrue(),
                () -> assertThat(ClassBeanConstructor.from(TestClass.class).isNotInstanced()).isFalse()
        );
    }

    @Test
    @DisplayName("생성자의 파라미터")
    void parameterTypes() {
        //given
        ClassBeanConstructor constructor = ClassBeanConstructor.from(TestClass.class);
        //when, then
        assertThat(constructor.parameterTypes()).isEmpty();
    }

    @Test
    @DisplayName("인스턴스화")
    void instantiate() {
        //given
        ClassBeanConstructor constructor = ClassBeanConstructor.from(TestClass.class);
        //when, then
        assertThat(constructor.instantiate(Collections.emptyList())).isInstanceOf(TestClass.class);
    }

    private interface TestInterface {
    }

    private static class TestClass {
    }
}
