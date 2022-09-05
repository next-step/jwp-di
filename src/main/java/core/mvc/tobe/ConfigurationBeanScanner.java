package core.mvc.tobe;

import core.di.factory.BeanFactory;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        this.beanFactory.register(clazz);
    }

}
