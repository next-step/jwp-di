package core.mvc.tobe;

import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationScanner implements BeanScanner {

    private static final Logger log = LoggerFactory.getLogger(AnnotationScanner.class);

    private static final List<Class<? extends Annotation>> TYPE_ANNOTATIONS =
            Arrays.asList(Repository.class, Service.class, Controller.class, Component.class);

    private final Reflections reflections;

    public AnnotationScanner(Object... basePackages) {
        this.reflections = new Reflections(getParams(basePackages));
    }

    private Object[] getParams(Object... basePackages) {
        if (ArrayUtils.isEmpty(basePackages)) {
            final Set<Class<?>> typeWithComponentScans = getTypesWithComponentScan();
            return getBasePackagesWithComponentScan(typeWithComponentScans);
        }

        return basePackages;
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

    @Override
    public Set<Class<?>> getTypes() {
        return TYPE_ANNOTATIONS.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .collect(Collectors.toSet());
    }
}
