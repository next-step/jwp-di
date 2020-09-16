package core.di;

import core.di.config.ConfigurationBeanScanner;
import core.di.factory.BeanFactory;

public class ApplicationContext {

    private Object[] basePackage;
    private BeanFactory beanFactory;

    public ApplicationContext(Object... basePackage) {
        this.basePackage = basePackage;
        this.beanFactory = new BeanFactory();
        new ConfigurationBeanScanner(beanFactory).scan(basePackage);
    }

    public Object[] getBasePackage() {
        return basePackage;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
