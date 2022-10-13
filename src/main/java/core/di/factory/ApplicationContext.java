package core.di.factory;

import core.annotation.ComponentScan;

import java.util.Map;

public class ApplicationContext {

    private final Class<?> baseConfigurationClass;
    private ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Class<?> clazz) {
        this.baseConfigurationClass = clazz;
    }

    public void initialize() {
        ComponentScan componentScan = baseConfigurationClass.getAnnotation(ComponentScan.class);
        if (componentScan == null) {
            return;
        }

        Object[] basePackages = componentScan.basePackages();

        BeanFactory beanFactory = new BeanFactory();
        new ConfigurationBeanScanner(beanFactory, basePackages);
        beanFactory.initialize();

        classpathBeanScanner = new ClasspathBeanScanner(beanFactory, basePackages);
    }

    public Map<Class<?>, Object> getFactoryController() {
        return classpathBeanScanner.getFactoryController();
    }
}
