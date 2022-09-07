package core.di.factory;

import core.annotation.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassBeanRegisterTest {


    @DisplayName("상위 타입이 존재하면 상위타입정보도 모두 조회해 반환한다.")
    @Test
    void interfacesExistsInterfaces() {
        ClassBeanRegister register = new ClassBeanRegister(ChildClass.class);

        List<Class<?>> interfaces = register.interfaces();

        assertThat(interfaces).isNotEmpty()
                .contains(SuperClass.class, ChildClass.class);
    }

    @DisplayName("상위 타입이 없는 경우 해당 클래스 타입만을 반환한다.")
    @Test
    void interfacesNotExistsInterfaces() {
        ClassBeanRegister register = new ClassBeanRegister(SingleClass.class);

        List<Class<?>> interfaces = register.interfaces();

        assertThat(interfaces).isNotEmpty()
                .containsExactly(SingleClass.class);
    }

    @DisplayName("getParameterTypes 메서드는 생성자 매개변수들의 타입정보를 반환한다.")
    @Test
    void itIsReturnsParameterTypeInfoOfConstructor() {
        ClassBeanRegister register = new ClassBeanRegister(SingleClass.class);

        Class<?>[] parameterTypes = register.getParameterTypes();

        assertThat(parameterTypes).contains(String.class);
    }

    @DisplayName("getParameterCount 메서드는 생성자 매개변수의 갯수를 반환한다.")
    @Test
    void itIsReturnsParameterCountOfConstructor() {
        ClassBeanRegister register = new ClassBeanRegister(SingleClass.class);
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.register(register);

        assertThat(register.getParameterCount()).isEqualTo(1);
    }

    private static class SingleClass {
        String param;

        public SingleClass() {
            this.param = "defaultParam";
        }

        @Inject
        public SingleClass(String param) {
            this.param = param;
        }
    }
    private interface SuperClass {}
    private static class ChildClass implements SuperClass {
        String param;

        public ChildClass(String param) {
            this.param = param;
        }
    }
}
