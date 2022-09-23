package core.di.factory;

import core.di.factory.constructor.ClassBeanConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DisplayName("클래스 빈 생성")
class ClassBeanConstructorTest {

    @DisplayName("인스턴스화 성공")
    @Test
    void instance() {
        assertThatNoException().isThrownBy(
                () -> new ClassBeanConstructor(TestClass.class));
    }

    @DisplayName("클래스 없을시 인스턴스화 실패")
    @Test
    void fail_instance() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new ClassBeanConstructor(null));
    }

    @DisplayName("빈 인스턴스화 가능 여부 확인")
    @Test
    void fail_instantiate() {
        assertThat(new ClassBeanConstructor(TestClass.class)
                .isNotInstanced()).isFalse();
    }

    @DisplayName("빈 인스턴스화 성공")
    @Test
    void instantiate() {
        ClassBeanConstructor constructor = new ClassBeanConstructor(TestClass.class);

        assertThat(constructor.instantiate(Collections.emptyList()))
                .isInstanceOf(TestClass.class);
    }

    private static class TestClass {
    }
}
