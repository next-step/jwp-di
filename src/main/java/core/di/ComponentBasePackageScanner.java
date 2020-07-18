package core.di;

import core.annotation.AnnotationScanner;
import core.annotation.ComponentScan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ComponentBasePackageScanner implements Scanner<Object> {

    private static final Class<ComponentScan> COMPONENT_SCAN_ANNOTATION = ComponentScan.class;

    @Getter
    private Set<Object> basePackages = new HashSet<>();
    private Reflections wholeReflections = new Reflections("");

    public ComponentBasePackageScanner(Object... basePackage) {
        this.wholeReflections = new Reflections(basePackage);
    }

    @Override
    public Set<Object> scan() {
        AnnotationScanner annotationScanner = new AnnotationScanner(COMPONENT_SCAN_ANNOTATION);
        Set<Class<? extends Annotation>> annotationsAnnotatedComponentScan = annotationScanner.scan();

        Set<Class<?>> classesAnnotatedComponentScan = wholeReflections.getTypesAnnotatedWith(COMPONENT_SCAN_ANNOTATION, true);

        registerBasePackageOfComponentScanClasses(classesAnnotatedComponentScan);
        registerPackageOfClassesWithoutBasePackage(classesAnnotatedComponentScan);
        registerBasePackageOfAnnotations(annotationsAnnotatedComponentScan);

        return new HashSet<>(this.basePackages);
    }

    private void registerBasePackageOfComponentScanClasses(Set<Class<?>> classesAnnotatedComponentScan) {
        for (Class<?> componentScanClass : classesAnnotatedComponentScan) {
            ComponentScan componentScanAnnotation = componentScanClass.getAnnotation(COMPONENT_SCAN_ANNOTATION);
            String[] basePackage = componentScanAnnotation.basePackage();

            basePackages.addAll(Arrays.asList(basePackage));
        }
    }

    private void registerPackageOfClassesWithoutBasePackage(Set<Class<?>> classesAnnotatedComponentScan) {
        Set<Class<?>> classesWithoutBasePackage = classesAnnotatedComponentScan.stream()
                .filter(clazz -> !clazz.isAnnotation())
                .filter(clazz -> isEmptyBasePackageOf(clazz.getAnnotation(COMPONENT_SCAN_ANNOTATION)))
                .collect(Collectors.toSet());

        registerBasePackage(classesWithoutBasePackage);
    }

    private boolean isEmptyBasePackageOf(ComponentScan componentScan) {
        return componentScan.basePackage().length == 0;
    }

    private void registerBasePackageOfAnnotations(Set<Class<? extends Annotation>> annotationsAnnotatedComponentScan) {
        for (Class<? extends Annotation> annotation : annotationsAnnotatedComponentScan) {
            Set<Class<?>> annotatedClasses = wholeReflections.getTypesAnnotatedWith(annotation, true);
            registerBasePackage(annotatedClasses);
        }
    }

    private void registerBasePackage(Set<Class<?>> classes) {
        classes.stream()
                .filter(clazz -> !clazz.isAnnotation())
                .forEach(clazz -> this.basePackages.add(clazz.getPackage().getName()));
    }

}
