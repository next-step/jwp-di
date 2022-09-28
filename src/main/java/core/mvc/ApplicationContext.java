package core.mvc;

import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;

public class ApplicationContext {
    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?> config) {
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(config);

        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(configurationBeanScanner.getBasePackages());
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
