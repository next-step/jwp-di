package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.bean.Bean;
import core.di.factory.bean.ClassBean;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ClasspathBeanScanner {
    private static final Collection<Class<? extends Annotation>> SCAN_ANNOTATION_CLASSES = Set.of(Controller.class, Service.class, Repository.class);

    private final Collection<Reflections> reflections;

    public ClasspathBeanScanner(Collection<Class<?>> configurations) {
        this.reflections = packageScanReflections(configurations);
    }

    Collection<Bean> scan() {
        return reflections.stream()
                .flatMap(reflection -> SCAN_ANNOTATION_CLASSES.stream().map(reflection::getTypesAnnotatedWith))
                .flatMap(Collection::stream)
                .map(ClassBean::new)
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
}
