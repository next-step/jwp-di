package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final BeanDefinitions beanDefinitions;

    public ConfigurationBeanScanner(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void register(Class<?>... configurations) {
        beanDefinitions.addConfigurations(Arrays.stream(configurations)
                .collect(Collectors.toList()));
    }
}
