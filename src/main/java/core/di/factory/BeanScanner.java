package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanScanner {
    private static final Collection<Class<? extends Annotation>> SCAN_ANNOTATION_CLASSES = Set.of(Controller.class, Service.class, Repository.class);

    private final Reflections reflections;

    public BeanScanner(Object... basePackages) {
        this.reflections = new Reflections(basePackages, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.ConstructorsAnnotated);
    }

    Collection<Class<?>> scan() {
        return classesWithScanAnnotation();
    }

    <T> Class<? extends T> subTypeOf(Class<T> target) {
        Collection<Class<? extends T>> subTypes = reflections.getSubTypesOf(target);
        return subTypes.iterator().next();
    }

    private Set<Class<?>> classesWithScanAnnotation() {
        return SCAN_ANNOTATION_CLASSES
                .stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
