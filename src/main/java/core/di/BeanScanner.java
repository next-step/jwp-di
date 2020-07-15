package core.di;

import com.google.common.collect.Sets;
import core.annotation.Component;
import core.annotation.WebApplication;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanScanner {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";
    private static final Class<WebApplication> WEB_APPLICATION_ANNOTATION = WebApplication.class;
    private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

    private Object[] basePackage = {};
    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        this.basePackage = basePackage;
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> scan() {
        this.basePackage = findBasePackage();
        this.reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<? extends Annotation>> componentAnnotations = getComponentAnnotations();
        return getTypesAnnotatedWith(componentAnnotations);
    }

    private Object[] findBasePackage() {
        if (this.basePackage.length != 0) {
            return this.basePackage;
        }

        Reflections wholeReflections = new Reflections("");
        Object[] wholeBasePackage = wholeReflections.getTypesAnnotatedWith(WEB_APPLICATION_ANNOTATION)
                .stream()
                .map(this::findBasePackages)
                .flatMap(Arrays::stream)
                .toArray();

        if (wholeBasePackage.length == 0) {
            throw new IllegalStateException("Base package not initialized");
        }

        return wholeBasePackage;
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

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Set<Class<? extends Annotation>> annotations) {
        Set<Class<?>> annotatedClasses = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            annotatedClasses.addAll(reflections.getTypesAnnotatedWith(annotation, true));
        }
        return annotatedClasses;
    }

    private String[] findBasePackages(Class<?> clazz) {
        String[] packages = clazz.getAnnotation(WEB_APPLICATION_ANNOTATION).basePackages();

        return packages.length == 0 ? new String[]{clazz.getPackage().getName()} : packages;
    }

}
