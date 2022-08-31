package core.di.factory;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanScanner {

    private final List<Class<? extends Annotation>> targetAnnotationClasses;

    public BeanScanner(List<Class<? extends Annotation>> targetAnnotationClasses) {
        this.targetAnnotationClasses = targetAnnotationClasses;
    }

    public Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(
                basePackage,
                Scanners.TypesAnnotated,
                Scanners.SubTypes,
                Scanners.MethodsAnnotated
        );

        return targetAnnotationClasses.stream()
                .flatMap(clazz -> reflections.getTypesAnnotatedWith(clazz).stream())
                .collect(Collectors.toSet());
    }

}
