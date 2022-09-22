package core.di;

import core.annotation.Component;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class ClassPathBeanDefinitionScanner {
    private static final Set<Class<? extends Annotation>> candidateAnnotations = Set.of(Controller.class, Service.class, Repository.class, Component.class, Configuration.class);

    private final BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.registry = beanDefinitionRegistry;
    }

    public void scan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> preInstantiatedBeans = new HashSet<>();
        candidateAnnotations.forEach(candidateAnnotation -> preInstantiatedBeans.addAll(reflections.getTypesAnnotatedWith(candidateAnnotation)));
        registry.registerClassPathBeans(preInstantiatedBeans);
    }
}
