package core.di;

import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathBeanDefinitionScanner implements BeanScanner {
    private static final int EMPTY = 0;
    private static final Set<Class<? extends Annotation>> candidateAnnotations = Set.of(Controller.class, Service.class, Repository.class, Component.class, Configuration.class);

    private final BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.registry = beanDefinitionRegistry;
    }

    @Override
    public void scan(Set<Class<?>> configurationClasses) {
        Reflections reflections = new Reflections(getBasePackages(configurationClasses));
        Set<Class<?>> preInstantiatedBeans = new HashSet<>();
        for (Class<? extends Annotation> candidateAnnotation : candidateAnnotations) {
            preInstantiatedBeans.addAll(reflections.getTypesAnnotatedWith(candidateAnnotation));
        }

        Set<BeanDefinition> classBeanDefinitions = preInstantiatedBeans.stream()
                .map(ClassBeanDefinition::new)
                .collect(Collectors.toSet());

        registry.registerClassPathBeans(classBeanDefinitions);
    }

    private Object[] getBasePackages(Set<Class<?>> configurationClasses) {
        return configurationClasses.stream()
                .filter(configurationClass -> configurationClass.isAnnotationPresent(ComponentScan.class))
                .map(configurationClass -> {
                    ComponentScan componentScan = configurationClass.getAnnotation(ComponentScan.class);
                    if (componentScan.value().length == EMPTY) {
                        return configurationClass.getPackageName();
                    }
                    return componentScan.value();
                }).toArray();
    }
}
