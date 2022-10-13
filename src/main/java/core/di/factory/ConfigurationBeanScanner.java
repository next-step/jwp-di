package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.definition.BeanDefinition;
import core.di.factory.definition.ConfigurationBeanDefinition;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Object[] basePackage) {
        var reflections = new Reflections(basePackage);
        var configClasses = reflections.getTypesAnnotatedWith(Configuration.class);

        configClasses.forEach(this::register);
    }

    public void register(Class<?> configClass) {
        Object instance = createInstance(configClass);

        Set<BeanDefinition> beanMethods = Arrays.stream(configClass.getDeclaredMethods())
            .filter(it -> it.isAnnotationPresent(Bean.class))
            .map(it -> new ConfigurationBeanDefinition(instance, it))
            .collect(Collectors.toSet());

        for (BeanDefinition beanDefinition : beanMethods) {
            beanFactory.addBean(beanDefinition);
        }
    }

    private Object createInstance(Class<?> configClass) {
        try {
            Constructor<?> constructor = configClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Configuration class를 생성할 수 없습니다" + configClass);
        }
    }
}
