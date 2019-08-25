package core.di.factory;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanScanner {

    private Reflections reflections;

    public BeanScanner(Object... basePackge) {
        reflections = new Reflections(basePackge);
    }

    public Set<Class<?>> scan(Class<? extends Annotation>... annotations) {
        return Arrays.stream(annotations)
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
