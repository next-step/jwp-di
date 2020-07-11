package core.di;

import core.annotation.Component;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class BeanScanner {

    private static final String BASE_ANNOTATION = Component.class.getPackage().getName();

    private final Object[] basePackage;

    public BeanScanner(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public Set<Class<?>> scan(Class<? extends Annotation> type) {
        Reflections reflections = new Reflections(BASE_ANNOTATION, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(type);
        annotations.add(type);
        return getTypesAnnotatedWith(annotations.toArray(new Class<?>[annotations.size()]))
            .stream().filter(aClass -> !aClass.isInterface())
            .collect(Collectors.toSet());
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<?>... annotations) {
        Reflections reflections = new Reflections(this.basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> classes = new HashSet<>();
        for (Class annotation : annotations) {
            classes.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return classes;
    }
}
