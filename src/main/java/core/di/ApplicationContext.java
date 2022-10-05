package core.di;

import core.di.factory.BeanFactory;
import core.di.scanner.ClasspathBeanScanner;
import core.di.scanner.ConfigurationBeanScanner;

public class ApplicationContext {

    private final BeanFactory beanFactory = new BeanFactory();

    public void initialize(Class<?> configurationClass) {
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner();

        String[] basePackages = configurationBeanScanner.getBasePackages(configurationClass);
        classpathBeanScanner.scan(beanFactory, basePackages);
        configurationBeanScanner.scan(beanFactory, basePackages);

        try {
            beanFactory.initialize();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

}
