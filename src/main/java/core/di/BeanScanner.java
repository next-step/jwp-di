package core.di;

import com.google.common.collect.Sets;
import core.annotation.Component;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanScanner implements Scanner<Class<?>> {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";
    private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

    private Set<Object> basePackage = new HashSet<>();
    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        this.basePackage = Collections.singleton(basePackage);
    }

    @Override
    public Set<Class<?>> scan() {
        ComponentBasePackageScanner basePackageScanner = new ComponentBasePackageScanner();
        this.basePackage = basePackageScanner.scan();
        this.reflections = new Reflections(this.basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<? extends Annotation>> componentAnnotations = getComponentAnnotations();
        return getTypesAnnotatedWith(componentAnnotations);
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
