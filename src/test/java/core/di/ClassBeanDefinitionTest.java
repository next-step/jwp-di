package core.di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ClassBeanDefinitionTest {
    @Test
    @DisplayName("ClassBeanDefinition 생성시 클래스 빈 정보가 null 인 경우 예외가 발생한다.")
    void throwExceptionWhenFieldNull() {
        assertThatThrownBy(() -> new ClassBeanDefinition(null)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("클래스 빈 타입의 경우, 메서드 빈 타입 정보를 가져오는 메서드 호출 시 예외가 발생한다.")
    void isMethodReturnTypeEqual() {
        BeanDefinition beanDefinition = new ClassBeanDefinition(ClassBeanDefinitionTest.class);
        assertAll(
                () -> assertThatThrownBy(() -> beanDefinition.getBeanMethod()).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> beanDefinition.methodReturnType()).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> beanDefinition.isMethodReturnTypeEqual(Integer.class)).isInstanceOf(IllegalArgumentException.class)
        );
    }
}
