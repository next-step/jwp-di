package core.di.factory;

import core.annotation.Component;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class BeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);
    private static final String ANNOTATION_PREFIX = "core.annotation";

    private Set<Class<?>> preInstanticateBeans;

    public BeanScanner(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> classes = ScannerUtils.getTypesAnnotatedWith(reflections, getComponentAnnotation());
        this.preInstanticateBeans = classes.stream()
                .filter(clazz -> !clazz.isAnnotation())
                .collect(Collectors.toSet());
    }

    private Class<? extends Annotation>[] getComponentAnnotation() {
        Reflections reflections = new Reflections(ANNOTATION_PREFIX, new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(Component.class);
        annotations.add(Component.class);

        return (Class<? extends Annotation>[]) annotations.toArray(new Class<?>[annotations.size()]);
    }
}
