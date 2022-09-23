package core.di.factory.scanner;

import core.annotation.ComponentScan;
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

public class ClassPathBeanScanner {
    private static final Collection<Class<? extends Annotation>> SCAN_ANNOTATIONS = Set.of(
            Controller.class, Service.class, Repository.class);

    private final Collection<Reflections> reflections;

    public ClassPathBeanScanner(Collection<Class<?>> configurations) {
        Assert.notNull(configurations, "configurations 값이 null이어선 안됩니다.");
        this.reflections = packageScanReflections(configurations);
    }

    private Collection<Reflections> packageScanReflections(Collection<Class<?>> configurations) {
        return configurations.stream()
                .filter(configuration -> configuration.isAnnotationPresent(ComponentScan.class))
                .map(configuration -> configuration.getDeclaredAnnotation(ComponentScan.class))
                .flatMap(componentScan -> Arrays.stream(componentScan.value()))
                .map(basePackage -> new Reflections(
                        basePackage, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.ConstructorsAnnotated))
                .collect(Collectors.toSet());
    }

    public Collection<BeanConstructor> scan() {
        return reflections.stream()
                .flatMap(reflection -> SCAN_ANNOTATIONS.stream()
                        .map(reflection::getTypesAnnotatedWith))
                .flatMap(Collection::stream)
                .map(ClassBeanConstructor::new)
                .collect(Collectors.toList());
    }
}
