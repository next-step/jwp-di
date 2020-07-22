package core.di.config;

import core.annotation.Configuration;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class AnnotationConfigurationMapping {

    private Set<Class<?>> typesAnnotatedWith;

    public AnnotationConfigurationMapping() {
        initiate();
    }

    public void initiate() {
        Reflections reflections = new Reflections("");
        typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
    }

    public Set<Class<?>> getTypesAnnotatedWith() {
        return new HashSet<>(typesAnnotatedWith);
    }
}
