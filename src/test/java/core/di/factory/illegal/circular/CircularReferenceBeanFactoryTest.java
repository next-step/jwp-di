package core.di.factory.illegal.circular;

import core.di.BeanScanner;
import core.di.factory.BeanFactory;
import core.di.factory.exception.CircularReferenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CircularReferenceBeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        BeanScanner beanScanner = new BeanScanner("core.di.factory.illegal.circular");
        Set<Class<?>> preInstantiateBeans = beanScanner.scan();

        beanFactory = new BeanFactory(preInstantiateBeans);
    }

    @Test
    public void circularReferenceException() {
        assertThrows(CircularReferenceException.class, () -> beanFactory.initialize());
    }

}
