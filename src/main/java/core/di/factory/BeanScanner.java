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

public class BeanScanner {

    private static final List<Class<? extends Annotation>> annotations =
            asList(Controller.class, Service.class, Repository.class, Component.class);

    public static Set<BeanDefinition> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return annotations.stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .map(BeanDefinition::new)
                .collect(Collectors.toSet());
    }

}
