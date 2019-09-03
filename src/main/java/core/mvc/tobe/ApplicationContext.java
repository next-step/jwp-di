package core.mvc.tobe;

import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;
import java.util.Map;

public class ApplicationContext {

    private BeanFactory beanFactory;

    public ApplicationContext(Class<?> configurationClass) {
        initContext(configurationClass);
    }

    private void initContext(Class<?> configurationClass){
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(configurationClass);

        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(configurationBeanScanner.getBasePath());
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beanFactory.getBean(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
