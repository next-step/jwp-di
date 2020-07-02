package core.di.factory;

import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;

import java.util.Map;
import java.util.Objects;

public class ApplicationContext {
    private final BeanFactory beanFactory;
    private final ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Class<?> configurationClass) {
        this(configurationClass, (String[]) null);
    }

    public ApplicationContext(Class<?> configurationClass, String ...basePackages) {
        beanFactory = new BeanFactory();

        if (Objects.nonNull(configurationClass)) {
            ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
            cbs.register(configurationClass);
            beanFactory.initialize();
        }

        classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(basePackages);
        clearResolvers();
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        HandlerBeanScanner handlerBeanScanner = new HandlerBeanScanner(beanFactory);
        return handlerBeanScanner.scan();
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    private void clearResolvers() {
        beanFactory.clearResolvers();
    }
}
