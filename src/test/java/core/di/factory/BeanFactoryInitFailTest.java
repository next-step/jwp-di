package core.di.factory;

import core.annotation.Component;
import core.annotation.Inject;
import core.di.exception.BeanCreateException;
import core.di.exception.NoDefaultConstructorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static core.utils.Generator.beanFactoryOf;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("빈 팩토리 초기화 실패 테스트")
public class BeanFactoryInitFailTest {

    @Test
    @DisplayName("@Inject 달린 생성자도, 기본 생성자가 없는경우 예외 발생")
    void noDefaultConstructorException() {
        assertThatExceptionOfType(NoDefaultConstructorException.class)
                .isThrownBy(() -> beanFactoryOf(Collections.singleton(NoDefaultConstructor.class)));
    }

    @Test
    @DisplayName("인터페이스를 구현한 클래스가 없는 경우 예외 발생")
    void noImplementClass() {
        assertThatExceptionOfType(BeanCreateException.class)
                .isThrownBy(() -> beanFactoryOf(Collections.singleton(NoImplementInterfaceClass.class)));
    }

    @Component
    public class NoDefaultConstructor {
        private int num;

        public NoDefaultConstructor(int num) {
            this.num = num;
        }
    }

    @Component
    public class NoImplementInterfaceClass {
        @Inject
        public NoImplementInterfaceClass(NoImplementClassInterface noImplementClassInterface) {

        }
    }

    public interface NoImplementClassInterface {

    }
}
