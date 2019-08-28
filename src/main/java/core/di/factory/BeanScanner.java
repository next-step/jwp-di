package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanScanner {

    private final List<Class<? extends Annotation>> TARGET_ANNOTATIONS = Arrays.asList(Controller.class
            , Service.class
            , Repository.class);

    private final Reflections reflections;

    public BeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> getPreInstanticateClasses() {
        return TARGET_ANNOTATIONS.stream()
                .map(this::getClassesByAnnotation)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }


}
