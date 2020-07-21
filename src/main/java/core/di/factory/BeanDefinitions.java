package core.di.factory;

import core.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanDefinitions {

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();

    public void addAllPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beanDefinitions.put(preInstantiateBean, new ClasspathBeanDefinition(preInstantiateBean));
        }
    }

    public void addAllConfigurations(List<Class<?>> configurations) {
        for (Class<?> configuration : configurations) {
            registerConfiguration(configuration);
        }

    }

    private void registerConfiguration(Class<?> configuration) {
        List<Method> beanMethods = getBeanMethods(configuration);
        for (Method beanMethod : beanMethods) {
            beanDefinitions.put(beanMethod.getReturnType(), new ConfigurationBeanDefinition(configuration, beanMethod));
        }
    }

    private List<Method> getBeanMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> m.isAnnotationPresent(Bean.class))
                .sorted(Comparator.comparing(m -> m.getParameters().length))
                .collect(Collectors.toList());
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
