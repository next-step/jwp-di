package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.constructor.BeanConstructor;
import core.di.factory.constructor.ClassBeanConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

final class ClasspathBeanScanner {

    private static final Collection<Class<? extends Annotation>> SCAN_ANNOTATION_CLASSES = Set.of(Controller.class, Service.class, Repository.class);

    private final Collection<Reflections> reflections;

    private ClasspathBeanScanner(Collection<Class<?>> configurations) {
        Assert.notNull(configurations, "'configurations' must not be null");
        Assert.isTrue(isAnnotatedConfiguration(configurations), String.format("'configurations(%s)' must be annotated Configuration", configurations));
        this.reflections = packageScanReflections(configurations);
    }

    static ClasspathBeanScanner from(Collection<Class<?>> configurations) {
        return new ClasspathBeanScanner(configurations);
    }

    Collection<BeanConstructor> scan() {
        return reflections.stream()
                .flatMap(reflection -> SCAN_ANNOTATION_CLASSES.stream().map(reflection::getTypesAnnotatedWith))
                .flatMap(Collection::stream)
                .map(ClassBeanConstructor::from)
                .collect(Collectors.toList());
    }

    private Collection<Reflections> packageScanReflections(Collection<Class<?>> configurations) {
        return configurations.stream()
                .filter(configuration -> configuration.isAnnotationPresent(ComponentScan.class))
                .map(configuration -> configuration.getDeclaredAnnotation(ComponentScan.class))
                .flatMap(componentScan -> Arrays.stream(componentScan.value()))
                .map(basePackage -> new Reflections(basePackage, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.ConstructorsAnnotated))
                .collect(Collectors.toSet());
    }

    private boolean isAnnotatedConfiguration(Collection<Class<?>> configurations) {
        return configurations.stream()
                .allMatch(configuration -> configuration.isAnnotationPresent(Configuration.class));
    }
}
