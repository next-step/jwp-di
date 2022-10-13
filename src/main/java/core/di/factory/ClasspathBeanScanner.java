package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.definition.BeanDefinition;
import core.di.factory.definition.SimpleBeanDefinition;

public class ClasspathBeanScanner {

    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(Object... path) {
        var reflections = new Reflections(path);
        var beanClasses = findBeanClasses(reflections, Controller.class, Service.class, Repository.class);

        Set<BeanDefinition> beanDefinitions = beanClasses.stream()
            .map(SimpleBeanDefinition::new)
            .collect(Collectors.toSet());

        for (BeanDefinition beanDefinition : beanDefinitions) {
            beanFactory.addBean(beanDefinition);
        }
    }

    private Set<Class<?>> findBeanClasses(Reflections reflections, Class<? extends Annotation>... classes) {
        return Arrays.stream(classes)
            .flatMap(it -> reflections.getTypesAnnotatedWith(it).stream())
            .collect(Collectors.toSet());
    }
}
