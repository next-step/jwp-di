package core.mvc.tobe;

import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanScanner {

    private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

    private static final List<Class<? extends Annotation>> SORTED_ANNOTATIONS =
            Arrays.asList(Repository.class, Service.class, Controller.class, Component.class);

    private final Reflections reflections;

    public BeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
    }

    public BeanScanner() {
        final Set<Class<?>> typeWithComponentScans = getTypesWithComponentScan();
        final Object[] basePackages = getBasePackagesWithComponentScan(typeWithComponentScans);

        reflections = new Reflections(basePackages);
    }

    private Set<Class<?>> getTypesWithComponentScan() {
        final Reflections rootReflections = new Reflections("");
        return rootReflections.getTypesAnnotatedWith(ComponentScan.class);
    }

    private String[] getBasePackagesWithComponentScan(Set<Class<?>> componentScans) {
        return componentScans.stream()
                .map(componentScan -> componentScan.getAnnotation(ComponentScan.class))
                .flatMap(componentScan -> Stream.concat(
                        Arrays.stream(componentScan.value()),
                        Arrays.stream(componentScan.basePackages())
                ))
                .toArray(String[]::new);
    }

    public Set<Class<?>> getTypesAnnotatedWith() {
        return SORTED_ANNOTATIONS.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .collect(Collectors.toSet());
    }
}
