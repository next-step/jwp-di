package core.di.factory;

import core.annotation.Component;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentScanner {
    public static final List<Class<? extends Annotation>> targetAnnotations =
            Arrays.asList(Configuration.class, Component.class, Controller.class, Service.class, Repository.class);

    private ComponentScanner() {}

    public static Set<Class<?>> scan(Object... basePackage) {
        return scan(targetAnnotations, basePackage);
    }

    public static Set<Class<?>> scan(List<Class<? extends Annotation>> targetAnnotations, Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        return loadComponents(targetAnnotations, reflections);
    }

    private static Set<Class<?>> loadComponents(List<Class<? extends Annotation>> targetAnnotations, Reflections reflections) {
        return targetAnnotations.stream()
                .map(annotation -> loadAnnotationAttachedClass(annotation, reflections))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static Set<Class<?>> loadAnnotationAttachedClass(Class<? extends Annotation> annotation, Reflections reflections) {
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
