package core.mvc;

import core.annotation.ComponentScan;
import core.util.ReflectionUtils;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentScanAnnotationParser {
    private final Set<Class<?>> typesAnnotatedWith;

    public ComponentScanAnnotationParser() {
        Reflections reflections = new Reflections();
        typesAnnotatedWith = ReflectionUtils.getTypesAnnotatedWith(reflections, ComponentScan.class);
    }


    public Object[] getBasePackages() {
        return typesAnnotatedWith.stream()
                                  .map(aClass -> aClass.getAnnotation(ComponentScan.class).value())
                                  .flatMap(Stream::of)
                                  .collect(Collectors.toList()).toArray();
    }
}
