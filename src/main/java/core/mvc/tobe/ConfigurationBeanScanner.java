package core.mvc.tobe;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScanner.class);


    private BeanFactory beanFactory;
    private Object[] basePackages;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?>... annotatedClasses) {
        Map<Class<?>, BeanDefinition> beanDefinitions = getBeanDefinitions(annotatedClasses);
        beanFactory.register(beanDefinitions);
        this.basePackages = findBasePackages(annotatedClasses);
    }

    private Map<Class<?>, BeanDefinition> getBeanDefinitions(Class<?>[] annotatedClasses) {
        Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> clazz : annotatedClasses) {
            beanDefinitions.putAll(getBeanDefinitionsByConfiguration(clazz));
        }

        return beanDefinitions;
    }

    private Map<Class<?>, BeanDefinition> getBeanDefinitionsByConfiguration(Class<?> clazz) {
        Object configurationObject = instantiateConfiguration(clazz);
        Map<Class<?>, Method> beanMethod = getBeanTypes(clazz);
        Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Map.Entry<Class<?>, Method> entry : beanMethod.entrySet()) {
            ConfigurationBeanDefinition configurationBeanDefinition = getBeanDefinition(configurationObject, entry);
            Class<?> beanType = entry.getKey();
            beanDefinitions.put(beanType, configurationBeanDefinition);
        }

        return beanDefinitions;
    }

    private ConfigurationBeanDefinition getBeanDefinition(Object configurationObject, Map.Entry<Class<?>, Method> entry) {
        Method beanCreateMethod = entry.getValue();
        return new ConfigurationBeanDefinition(configurationObject, beanCreateMethod);
    }

    private Map<Class<?>, Method> getBeanTypes(Class<?> annotatedClasses) {
        return Stream.of(annotatedClasses)
                .map(Class::getDeclaredMethods)
                .flatMap(Stream::of)
                .filter(methods -> methods.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(Method::getReturnType, method -> method));
    }

    private Object instantiateConfiguration(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage());
            throw new CreateInstanceFailException();
        }
    }

    private Object[] findBasePackages(Class<?>[] annotatedClasses) {
        return Arrays.stream(annotatedClasses)
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .flatMap(Stream::of)
                .toArray();
    }

    public Object[] getBasePackages() {
        return this.basePackages;
    }
}
