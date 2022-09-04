package core.di;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.ConfigurationBeanScanner;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import java.util.Map;
import java.util.Set;

public class BeanContext {

    private final BeanFactory beanFactory;
    private final String basePackage;
    private final BeanScanner beanScanner;

    public BeanContext(final BeanFactory beanFactory, final String basePackage) {
        this.beanFactory = beanFactory;
        this.basePackage = basePackage;
        this.beanScanner = new BeanScanner(ComponentClassesScanner.scanBasePackages(basePackage));
    }

    public void initialize() {
        final Set<Class<?>> configurationClasses = ConfigurationAnnotatedClassesScanner.scan(basePackage);
        final ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(configurationClasses);
        configurationBeanScanner.scan(beanFactory);

        beanScanner.scan(beanFactory);
    }

    public Map<HandlerKey, HandlerExecution> getHandlerExecutions() {
        return beanScanner.getHandlerExecutions(beanFactory);
    }

}
