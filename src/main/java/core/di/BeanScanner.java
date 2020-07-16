package core.di;

import com.google.common.collect.Sets;
import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.WebApplication;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanScanner {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";
    private static final Class<WebApplication> WEB_APPLICATION_ANNOTATION = WebApplication.class;
    private static final Class<ComponentScan> COMPONENT_SCAN_ANNOTATION = ComponentScan.class;
    private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

    // TODO: 2020/07/16 to naming
    private Object[] basePackage = {};
    private Set<Object> basePackages = new HashSet<>();
    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        this.basePackages = Collections.singleton(basePackage);
    }

    public Set<Class<?>> scan() {
        this.basePackages = findBasePackage();
        this.reflections = new Reflections(this.basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<? extends Annotation>> componentAnnotations = getComponentAnnotations();
        return getTypesAnnotatedWith(componentAnnotations);
    }

    // TODO: 2020/07/16 to refactor
    private Set<Object> findBasePackage() {
        if (this.basePackages.isEmpty()) {
            return this.basePackages;
        }

        Reflections wholeReflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = wholeReflections.getTypesAnnotatedWith(COMPONENT_SCAN_ANNOTATION);

        Set<Class<?>> collect = typesAnnotatedWith.stream()
                .map(this::temptemp)
                .filter(this::isEmptyBasePackages)
                .collect(Collectors.toSet());

        Set<Class<?>> classes = temp(wholeReflections, collect);

        Set<Object> wholeBasePackage = classes.stream()
                .map(this::findBasePackages)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        if (wholeBasePackage.isEmpty()) {
            throw new IllegalStateException("Base package not initialized");
        }

        return wholeBasePackage;
    }

    // TODO: 2020/07/16 지정된 basePackage 저장
    private Class<?> temptemp(Class<?> clazz) {
        ComponentScan annotation = clazz.getAnnotation(COMPONENT_SCAN_ANNOTATION);
        if (annotation == null || isEmptyBasePackages(clazz)) {
            return clazz;
        }

        String[] basePackages = annotation.basePackages();
        this.basePackages.addAll(Arrays.asList(basePackages));
        return clazz;
    }

    private boolean isEmptyBasePackages(Class<?> clazz) {
        ComponentScan annotation = clazz.getAnnotation(COMPONENT_SCAN_ANNOTATION);
        if (annotation == null) {
            return true;
        }

        return annotation.basePackages().length == 0;
    }

    // TODO: 2020/07/16 구현체의 package 찾기
    private Set<Class<?>> temp(Reflections reflections, Set<Class<?>> classes) {
        Set<Class<?>> componentScanClasses = new HashSet<>();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotation()) {
                Set<Class<?>> scanned = temp(reflections, reflections.getTypesAnnotatedWith((Class<? extends Annotation>) clazz));
                componentScanClasses.addAll(scanned);
                continue;
            }

            componentScanClasses.add(clazz);
        }

        return componentScanClasses;
    }

    private String[] findBasePackages(Class<?> clazz) {
        return new String[]{clazz.getPackage().getName()};
    }

    private Set<Class<? extends Annotation>> getComponentAnnotations() {
        Reflections annotationReflections = new Reflections(ANNOTATION_BASE_PACKAGE);
        Set<Class<?>> componentClasses = annotationReflections.getTypesAnnotatedWith(COMPONENT_ANNOTATION);

        Set<Class<? extends Annotation>> annotations = componentClasses.stream()
                .filter(Class::isAnnotation)
                .map(clazz -> (Class<? extends Annotation>) clazz)
                .collect(Collectors.toSet());

        annotations.add(COMPONENT_ANNOTATION);
        return annotations;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Set<Class<? extends Annotation>> annotations) {
        Set<Class<?>> annotatedClasses = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            annotatedClasses.addAll(reflections.getTypesAnnotatedWith(annotation, true));
        }
        return annotatedClasses;
    }

}
