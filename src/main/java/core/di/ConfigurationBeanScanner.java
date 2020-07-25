package core.di;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner implements Scanner<Class<?>> {

    private static final Class<Configuration> CONFIGURATION_ANNOTATION = Configuration.class;
    private static final Class<Bean> BEAN_ANNOTATION = Bean.class;

    private Reflections reflections = new Reflections("");
    private final Map<Class<?>, Method> beanCreationMethods = new HashMap<>();
    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Set<Class<?>> scan(Object... basePackage) {
        this.reflections = new Reflections(basePackage);

        Set<Class<?>> configurationClasses = this.reflections.getTypesAnnotatedWith(CONFIGURATION_ANNOTATION, true);
        for (Class<?> configurationClass : configurationClasses) {
            registerBeanCreationMethods(configurationClass);
        }

        Set<Class<?>> preInstantiateBeans = beanCreationMethods.keySet();
        this.beanFactory.registerPreInstantiateBeans(preInstantiateBeans);
        return preInstantiateBeans;
    }

    public boolean contains(Class<?> preInstantiateBean) {
        return beanCreationMethods.containsKey(preInstantiateBean);
    }

    public Method getBeanCreationMethod(Class<?> preInstantiateBean) {
        return beanCreationMethods.get(preInstantiateBean);
    }

    private void registerBeanCreationMethods(Class<?> configurationClass) {
        Set<Method> beanMethods = findBeanCreationMethods(configurationClass);
        for (Method beanMethod : beanMethods) {
            registerBeanCreationMethod(beanMethod);
        }
    }

    private Set<Method> findBeanCreationMethods(Class<?> configurationClass) {
        return Arrays.stream(configurationClass.getMethods())
                .filter(method -> method.isAnnotationPresent(BEAN_ANNOTATION))
                .collect(Collectors.toSet());
    }

    private void registerBeanCreationMethod(Method beanMethod) {
        Class<?> beanType = beanMethod.getReturnType();
        if (beanCreationMethods.containsKey(beanType)) {
            throw new IllegalStateException("Configuration bean return type is duplicate.");
        }

        this.beanCreationMethods.put(beanMethod.getReturnType(), beanMethod);
    }

}
