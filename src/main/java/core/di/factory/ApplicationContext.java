package core.di.factory;

import core.annotation.ComponentScan;

public class ApplicationContext {
    private Class<?> configuration;

    private Object[] basePackages;

    public ApplicationContext(Class<?> configuration) {
        this.configuration = configuration;
        initializeBasePackages();
    }

    private void initializeBasePackages() {
        ComponentScan cs = configuration.getDeclaredAnnotation(ComponentScan.class);
        basePackages = cs.basePackages();
    }

    public BeanFactory initialize() {
        BeanFactory beanFactory = new BeanFactory();
        initializeConfigBeans(beanFactory);
        initializeClassPathBeans(beanFactory);

        return beanFactory;
    }

    private void initializeConfigBeans(BeanFactory beanFactory) {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(configuration);
        beanFactory.initializeConfigBeans();
    }

    private void initializeClassPathBeans(BeanFactory beanFactory) {
        ClassPathBeanScanner cpbs = new ClassPathBeanScanner(beanFactory);
        cpbs.doScan(basePackages);
        beanFactory.initializeClassPathBeans();
    }
}