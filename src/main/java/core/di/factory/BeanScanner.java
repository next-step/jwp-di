package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class BeanScanner {
    private static final Set<Class<? extends Annotation>> candidateAnnotations = Set.of(Controller.class, Service.class, Repository.class);

    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        this.reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> scan() {
        return getTypesAnnotatedWith(candidateAnnotations);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Set<Class<? extends Annotation>> candidateAnnotations) {
        Set<Class<?>> preInstantiatedBeans = new HashSet<>();
        candidateAnnotations.forEach(candidateAnnotation -> preInstantiatedBeans.addAll(reflections.getTypesAnnotatedWith(candidateAnnotation)));
        return preInstantiatedBeans;
    }
}
