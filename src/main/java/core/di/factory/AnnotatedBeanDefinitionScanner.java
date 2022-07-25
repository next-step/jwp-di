package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AnnotatedBeanDefinitionScanner implements BeanDefinitionScanner {

    private final BeanFactory beanFactory;
    private final Set<Class<?>> configurationClasses = new HashSet<>();

    public AnnotatedBeanDefinitionScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void registerConfigurationClasses(Set<Class<?>> clazz) {
        for (Class<?> configurationClass : clazz) {
            if (!configurationClass.isAnnotationPresent(Configuration.class)) {
                throw new IllegalArgumentException();
            }

            configurationClasses.add(configurationClass);
        }
    }

    @Override
    public void scan() {
        for (Class<?> configurationClass : configurationClasses) {
            Arrays.stream(configurationClass.getDeclaredMethods())
                    .filter(it -> it.isAnnotationPresent(Bean.class))
                    .forEach(method -> beanFactory.registerBeanDefinition(method.getReturnType(),
                            new AnnotatedBeanDefinition(configurationClass, method)));
        }
    }
}
