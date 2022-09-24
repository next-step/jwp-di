package core.di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MethodBeanDefinitionTest {
    Integer testMethod() {
        return 0;
    }

    @Test
    @DisplayName("MethodBeanDefinition 생성시 클래스 빈 정보가 null 이거나 메서드 빈 정보가 null 인 경우 예외가 발생한다.")
    void throwExceptionWhenFieldNull() throws NoSuchMethodException {
        Method testMethod = MethodBeanDefinitionTest.class.getDeclaredMethod("testMethod");
        assertAll(
                () -> assertThatThrownBy(() -> new MethodBeanDefinition(null, null)).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new MethodBeanDefinition(null, testMethod)).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new MethodBeanDefinition(MethodBeanDefinitionTest.class, null)).isInstanceOf(IllegalArgumentException.class)
        );
    }


    @Test
    @DisplayName("메서드 빈 타입의 경우, 메서드 리턴 타입이 같은지 확인한다.")
    void isMethodReturnTypeEqual() throws NoSuchMethodException {
        BeanDefinition beanDefinition = new MethodBeanDefinition(MethodBeanDefinitionTest.class, MethodBeanDefinitionTest.class.getDeclaredMethod("testMethod"));
        assertAll(
                () -> assertThat(beanDefinition.isMethodReturnTypeEqual(Integer.class)).isTrue(),
                () -> assertThat(beanDefinition.isMethodReturnTypeEqual(Long.class)).isFalse()
        );
    }
}
