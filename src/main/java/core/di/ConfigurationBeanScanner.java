package core.di;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.DefaultBeanDefinition;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner implements BeanScanner<Class<?>> {

    private static final Class<Configuration> CONFIGURATION_ANNOTATION = Configuration.class;
    private static final Class<Bean> BEAN_ANNOTATION = Bean.class;

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> preInstantiateBeans = Sets.newHashSet();

        Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(CONFIGURATION_ANNOTATION, true);
        for (Class<?> configurationClass : configurationClasses) {
            Set<Method> beanMethods = findBeanCreationMethods(configurationClass);
            registerBeanDefinitions(beanMethods);

            preInstantiateBeans.addAll(getReturnTypeOf(beanMethods));
        }

        return preInstantiateBeans;
    }

    private void registerBeanDefinitions(Set<Method> beanMethods) {
        for (Method beanMethod : beanMethods) {
            Class<?> beanType = beanMethod.getReturnType();
            this.beanFactory.registerBeanDefinition(beanType, new DefaultBeanDefinition(beanType, beanMethod));
        }
    }

    private Set<Method> findBeanCreationMethods(Class<?> configurationClass) {
        return Arrays.stream(configurationClass.getMethods())
                .filter(method -> method.isAnnotationPresent(BEAN_ANNOTATION))
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getReturnTypeOf(Set<Method> methods) {
        return methods.stream()
                .map(Method::getReturnType)
                .collect(Collectors.toSet());
    }

}
