package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class BeanScanner {

    private static final Collection<Class<? extends Annotation>> SCAN_ANNOTATION_CLASSES = Set.of(Controller.class, Service.class, Repository.class);

    private final Reflections reflections;

    private BeanScanner(Object... basePackages) {
        Assert.notNull(basePackages, "basePackage must not be null");
        this.reflections = new Reflections(basePackages, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.ConstructorsAnnotated);
    }

    public static BeanScanner packages(Object... basePackages) {
        return new BeanScanner(basePackages);
    }

    Collection<Class<?>> scan() {
        return classesWithScanAnnotation();
    }

    <T> Class<? extends T> subTypeOf(Class<T> target) {
        Collection<Class<? extends T>> subTypes = reflections.getSubTypesOf(target);
        validateSize(target, subTypes);
        return subTypes.iterator().next();
    }

    private Set<Class<?>> classesWithScanAnnotation() {
        return SCAN_ANNOTATION_CLASSES
                .stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private <T> void validateSize(Class<T> target, Collection<Class<? extends T>> subTypes) {
        if (subTypes.size() != 1) {
            throw new NoUniqueBeanDefinitionException(target, subTypes.size(), String.format("target(%S) subTypes size must be one", target));
        }
    }
}
