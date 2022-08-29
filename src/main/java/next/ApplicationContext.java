package next;

import core.config.WebMvcConfiguration;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;

import java.util.Set;

public class ApplicationContext {
    private final Class<? extends WebMvcConfiguration> configurationClazz;

    private BeanFactory beanFactory;

    public ApplicationContext(Class<? extends WebMvcConfiguration> configurationClazz) {
        this.configurationClazz = configurationClazz;
    }

    public void initialize() {
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(configurationClazz);

        ClasspathBeanScanner classPathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classPathBeanScanner.scan(configurationClazz);

        beanFactory.initialize();

    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    public Set<Class<?>> getControllers() {
        return beanFactory.getControllers();
    }
}
