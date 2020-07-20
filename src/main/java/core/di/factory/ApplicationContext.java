package core.di.factory;

import core.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Map;

public class ApplicationContext {

    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?>... configurations) {
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(configurations);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan(getBasePackages(configurations));
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
