package core.di.factory;

import core.di.exception.BeanDuplicationException;
import core.di.factory.duplication.AnotherClass;
import core.di.factory.duplication.OneClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("하나의 타입에 여러개의 빈이 생성되는 경우")
public class BeanDuplicationTest {

    @Test
    @DisplayName("하나의 인터페이스를 구현하는 두개의 구현 클래스가 있는 경우 예외 발생")
    void beanDuplicationTest() {
        assertThatExceptionOfType(BeanDuplicationException.class)
                .isThrownBy(() -> BeanFactory.init(
                        new LinkedHashSet<>(Arrays.asList(OneClass.class, AnotherClass.class))
                ));
    }
}
