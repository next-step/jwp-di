package core.di.factory;

import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created By kjs4395 on 7/18/20
 */
public class ConfigurationScannerTest {

    @Test
    public void configurationScannerTest() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner2 cbs = new ConfigurationBeanScanner2(beanFactory);
        cbs.register(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
