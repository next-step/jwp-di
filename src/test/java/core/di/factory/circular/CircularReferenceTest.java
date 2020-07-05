package core.di.factory.circular;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.exception.BeanCurrentlyInCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CircularReferenceTest {
    private BeanFactory beanFactory;
    private BeanScanner beanScanner;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanScanner = new BeanScanner();
        beanFactory = new BeanFactory(beanScanner.scan("core.di.factory.circular"));
    }

    @DisplayName("순환참조가 일어나면 에러가 발생한다.")
    @Test
    public void circularReference() throws Exception {
        assertThatExceptionOfType(BeanCurrentlyInCreationException.class).isThrownBy(() -> {
            beanFactory.initialize();
        });
    }

}
