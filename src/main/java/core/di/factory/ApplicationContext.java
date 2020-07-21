package core.di.factory;

import java.util.Map;
import java.util.Set;

public class ApplicationContext {

    private Object[] basePackages;
    private BeanFactory beanFactory;
    private ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Set<Class<?>> configurationClasses) {
        classpathBeanScanner = new ClasspathBeanScanner();
        basePackages = classpathBeanScanner.getBasePackagesWithComponentScan();
        beanFactory = new BeanFactory(classpathBeanScanner.scan(basePackages));
        beanFactory.instantiateConfiguration(configurationClasses);
        beanFactory.initialize();
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
