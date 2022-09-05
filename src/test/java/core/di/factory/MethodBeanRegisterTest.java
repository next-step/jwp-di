package core.di.factory;

import core.annotation.Bean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MethodBeanRegisterTest {

    @DisplayName("interfaces 메서드는 메서드의 반환타입의 인터페이스 목록을 반환한다.")
    @Test
    void itIsReturnsInterfaceListWithMethodReturnType() {
        Method[] methods = SingleClass.class.getDeclaredMethods();
        MethodBeanRegister register = new MethodBeanRegister(methods[0]);

        List<Class<?>> interfaces = register.interfaces();

        assertThat(interfaces).isNotEmpty()
                .contains(ParentDepthTwo.class, ChildClass.class);
    }

    @DisplayName("getParameterTypes 메서드는 해당 메서드의 매개변수 타입 정보를 반환한다.")
    @Test
    void itIsReturnsMethodParameterTypeInfoOfMethod() {
        Method[] methods = SingleClass.class.getDeclaredMethods();
        MethodBeanRegister register = new MethodBeanRegister(methods[0]);

        Class<?>[] parameterTypes = register.getParameterTypes();

        assertThat(parameterTypes).hasSize(1)
                .contains(ParentDepthTwo.class);
    }

    @DisplayName("getParameterCount 메서느는 해당 메서드의 매개변수 갯수를 반환한다.")
    @Test
    void itIsReturnsMethodParameterCountOfMethod() {
        Method[] methods = SingleClass.class.getDeclaredMethods();
        MethodBeanRegister register = new MethodBeanRegister(methods[0]);

        assertThat(register.getParameterCount()).isEqualTo(1);
    }


    private interface ParentDepthTwo {}
    private static class ChildClass implements ParentDepthTwo {}

    private static class SingleClass {

        @Bean
        ChildClass childClass(ParentDepthTwo parentDepthTwo) {
            return new ChildClass();
        }
    }
}
