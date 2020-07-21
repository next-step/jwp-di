package core.circular;

import core.di.factory.BeanDefinitions;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.exception.BeanCurrentlyInCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CircularReferenceTest {
    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        BeanDefinitions beanDefinitions = new BeanDefinitions();
        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanDefinitions);
        cbds.doScan(CircularReferenceTestConfig.class);
        beanFactory = new BeanFactory(beanDefinitions);
    }

    @DisplayName("순환참조가 일어나면 에러가 발생한다.")
    @Test
    public void circularReference() throws Exception {
        assertThatExceptionOfType(BeanCurrentlyInCreationException.class).isThrownBy(() -> {
            beanFactory.initialize();
        });
    }

}
