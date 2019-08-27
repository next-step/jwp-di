package core.di.factory;

import org.reflections.Reflections;

import java.beans.BeanDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanScanner {

    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
    }

    public BeanDefinitions scan(Class<? extends Annotation>... annotations) {
        return new BeanDefinitions(Arrays.stream(annotations)
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .map(BeanDefinition::new)
                .collect(Collectors.toSet()));
    }

}
