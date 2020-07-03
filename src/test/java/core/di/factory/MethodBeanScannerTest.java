package core.di.factory;

import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(beanDefinitionRegistry.getBeanDefinitions(MyJdbcTemplate.class)).isNotEmpty();
        assertThat(beanDefinitionRegistry.getBeanDefinition("dataSource2")).isNotNull();
    }
}
