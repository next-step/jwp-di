package core.di.factory;

import core.di.factory.definition.BeanDefinitionRegistry;
import core.di.factory.scanner.BeanScannerRegistry;
import core.di.factory.scanner.ClassPathBeanScanner;
import core.di.factory.scanner.ConfigurationBeanScanner;

import java.util.Map;

public class ApplicationContext {
    private final BeanFactory beanFactory;
    private final BeanScannerRegistry beanScannerRegistry;

    public ApplicationContext(Class<?>... configurations) {
        BeanDefinitionRegistry beanDefinitions = new BeanDefinitionRegistry();
        beanScannerRegistry = new BeanScannerRegistry();
        beanFactory = new BeanFactory(beanDefinitions);
        beanScannerRegistry.addBeanScanner(new ConfigurationBeanScanner(beanDefinitions));
        beanScannerRegistry.addBeanScanner(new ClassPathBeanScanner(beanDefinitions));
        beanScannerRegistry.scan(configurations);
        beanFactory.register();
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
