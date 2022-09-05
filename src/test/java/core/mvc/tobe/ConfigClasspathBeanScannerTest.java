package core.mvc.tobe;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.BeanFactory;
import javax.sql.DataSource;
import next.config.DataConfiguration;
import org.junit.jupiter.api.Test;

class ConfigClasspathBeanScannerTest {

    @Test
    void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configBeanScanner.register(DataConfiguration.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

}
