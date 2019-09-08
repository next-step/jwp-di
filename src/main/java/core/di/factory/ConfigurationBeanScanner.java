package core.di.factory;

import core.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {
    private BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configuration) {
        Method[] methods = configuration.getDeclaredMethods();

        Map<Class<?>, Method> beanMethods = Stream.of(methods)
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(method -> method.getReturnType(), method -> method));

        beanFactory.registerBeanMethods(beanMethods);
        beanFactory.registerConfigurationClass(configuration);
    }
}
