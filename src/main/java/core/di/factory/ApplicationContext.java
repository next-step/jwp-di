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
        PreInstanceBeanHandler pibh = new PreInstanceBeanHandler();
        registerConfigBeans(pibh);
        registerClassPathBeans(pibh);

        beanFactory.registerConfigurationClass(configuration);
        beanFactory.registerPreInstanceBeanHandler(pibh);
        beanFactory.initializeBeans();

        return beanFactory;
    }

    private void registerConfigBeans(PreInstanceBeanHandler pibh) {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(pibh);
        cbs.register(configuration);

    }

    private void registerClassPathBeans(PreInstanceBeanHandler pibh) {
        ClassPathBeanScanner cpbs = new ClassPathBeanScanner(pibh);
        cpbs.doScan(basePackages);
    }
}