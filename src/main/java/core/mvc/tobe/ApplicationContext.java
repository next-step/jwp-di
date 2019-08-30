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
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(configurationClass);
        beanFactory.initialize();

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan(cbs.getBasePath());
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beanFactory.getBean(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
