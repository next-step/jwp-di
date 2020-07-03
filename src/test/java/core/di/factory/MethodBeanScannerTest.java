package core.di.factory;

import core.annotation.Bean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author KingCjy
 */
public class MethodBeanScannerTest {

    BeanDefinitionRegistry beanDefinitionRegistry;

    @BeforeEach
    public void setUp() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();

        MethodBeanScanner methodBeanScanner = new MethodBeanScanner(beanFactory);
        methodBeanScanner.scan("core.di.factory.example");

        beanDefinitionRegistry = beanFactory;
    }

    @Test
    public void registerMethodBeanDefinitionTest() {
        beanDefinitionRegistry.getBeanDefinition("dataSource");
    }
}
