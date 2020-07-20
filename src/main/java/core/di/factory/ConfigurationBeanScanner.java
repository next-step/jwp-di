package core.di.factory;

import java.util.Arrays;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?>... configurations) {
        beanFactory.addAllConfigurations(Arrays.asList(configurations));
    }
}
