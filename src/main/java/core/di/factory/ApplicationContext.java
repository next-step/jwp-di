package core.di.factory;

import core.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class ApplicationContext {
    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?>... configurations) {
        BeanDefinitions beanDefinitions = new BeanDefinitions();
        beanFactory = new BeanFactory(beanDefinitions);
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.register(configurations);
        ClassPathBeanScanner cdbs = new ClassPathBeanScanner(beanDefinitions);
        cdbs.doScan(getBasePackages(configurations));
        beanFactory.register();
    }

    private Object[] getBasePackages(Class<?>[] configurations) {
        return Arrays.stream(configurations)
                .filter(configuration -> configuration.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .flatMap(Stream::of)
                .toArray();
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
