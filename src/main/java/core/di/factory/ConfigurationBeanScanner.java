package core.di.factory;

import java.util.Arrays;

public class ConfigurationBeanScanner {

    private final BeanDefinitions beanDefinitions;

    public ConfigurationBeanScanner(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void register(Class<?>... configurations) {
        beanDefinitions.addAllConfigurations(Arrays.asList(configurations));
    }
}
