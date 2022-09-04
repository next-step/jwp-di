package core.di;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.google.common.collect.Sets;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;

public class ClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        List<Class<? extends Annotation>> annotationClasses = List.of(Controller.class, Service.class, Repository.class, Component.class);
        Set<Class<?>> preInstantiateBeans = Sets.newHashSet();
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            preInstantiateBeans.addAll(reflections.getTypesAnnotatedWith(annotationClass));
        }

        for (Class<?> beanClass : preInstantiateBeans) {
            registry.registerBeanDefinition(beanClass, new BeanDefinition(beanClass));
        }
    }
}
