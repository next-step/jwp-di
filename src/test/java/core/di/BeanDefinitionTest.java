package core.di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BeanDefinitionTest {
    Integer testMethod() {
        return 0;
    }

    @Test
    @DisplayName("BeanDefinition 생성시 빈 클래스 정보, 빈 메서드 정보 모두 null 이거나 빈 클래스 정보가 null 인 경우 예외가 발생한다.")
    void throwExceptionWhenFieldNull() throws NoSuchMethodException {
        Method testMethod = BeanDefinitionTest.class.getDeclaredMethod("testMethod");
        assertThatThrownBy(() -> new BeanDefinition(null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new BeanDefinition(null, testMethod)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("메서드 빈 타입의 경우, 메서드 리턴 타입이 같은지 확인한다.")
    void isMethodReturnTypeEqual() throws NoSuchMethodException {
        BeanDefinition beanDefinition = new BeanDefinition(BeanDefinitionTest.class, BeanDefinitionTest.class.getDeclaredMethod("testMethod"));
        assertAll(
                () -> assertThat(beanDefinition.isMethodReturnTypeEqual(Integer.class)).isTrue(),
                () -> assertThat(beanDefinition.isMethodReturnTypeEqual(Long.class)).isFalse()
        );
    }

    @Test
    @DisplayName("클래스 빈 타입일 경우 메서드 리턴 타입과 비교 시 예외가  발생한다.")
    void throwExceptionWhenCompareDiffType() {
        BeanDefinition beanDefinition = new BeanDefinition(BeanDefinitionTest.class);
        assertThatThrownBy(() -> beanDefinition.isMethodReturnTypeEqual(Integer.class)).isInstanceOf(IllegalArgumentException.class);
    }
}
