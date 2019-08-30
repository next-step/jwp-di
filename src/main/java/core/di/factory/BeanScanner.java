package core.di.factory;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public abstract class BeanScanner<T> {

    protected BeanDefinitionRegistry registry;

    protected BeanScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    private List<Class<? extends Annotation>> annotations =
            asList(Controller.class, Service.class, Repository.class, Component.class);

    public void scan(T... args) {
        doScan(args);
    }

    protected abstract void doScan(T... args);

    protected Set<BeanDefinition> scanByBasePackages(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages);
        return annotations.stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .map(BeanDefinition::new)
                .collect(Collectors.toSet());
    }


}
