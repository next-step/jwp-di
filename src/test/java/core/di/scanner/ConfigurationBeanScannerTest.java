package core.di.scanner;

import core.di.factory.BeanFactory;
import core.tconfiguration.MockApplicationConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class ConfigurationBeanScannerTest {

    ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner();

    @Test
    void getBasePackagesTest() {
        String[] actual = new String[]{"core, next"};
        String[] expected = configurationBeanScanner.getBasePackages(MockApplicationConfiguration.class);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void scanTest() {
        BeanFactory beanFactory = new BeanFactory();
        configurationBeanScanner.scan(beanFactory, "core.tconfiguration");

        Set<Class<?>> actual = beanFactory.getPreInstantiatedBeansFromConfiguration();
        Set<Class<?>> expectedPreInstantiatedClasses = new HashSet<>() {{
            add(MockApplicationConfiguration.class);
        }};

        Assertions.assertThat(actual).isEqualTo(expectedPreInstantiatedClasses);
    }

}
