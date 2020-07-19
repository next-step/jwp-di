package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.example.ExampleConfig;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ConfigurationBeanScannerTest {

    @Test
    void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionScanner cbds = new ConfigurationBeanDefinitionScanner(beanFactory);
        cbds.register(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
