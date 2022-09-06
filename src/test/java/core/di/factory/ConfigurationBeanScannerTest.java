package core.di.factory;

import core.di.ConfigurationBeanScanner;
import core.di.factory.example.ExampleConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.lang.reflect.InvocationTargetException;

public class ConfigurationBeanScannerTest {

    @Test
    void resister() throws InvocationTargetException, IllegalAccessException {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(ExampleConfig.class);
        beanFactory.initialize();

        Assertions.assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }

}
