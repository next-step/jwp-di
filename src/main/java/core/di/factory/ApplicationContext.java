package core.di.factory;

import core.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Map;

public class ApplicationContext {

    private final BeanFactory beanFactory;
    private final BeanDefinitions beanDefinitions;

    public ApplicationContext(Class<?>... configurations) {
        beanDefinitions = new BeanDefinitions();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.register(configurations);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanDefinitions);
        cbds.doScan(getBasePackages(configurations));
        beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.initialize();
    }

    private Object[] getBasePackages(Class<?>[] configurations) {
        return Arrays.stream(configurations)
                .filter(c -> c.isAnnotationPresent(ComponentScan.class))
                .map(a -> a.getAnnotation(ComponentScan.class))
                .map(ComponentScan::basePackages)
                .flatMap(Arrays::stream).toArray();
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
