package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;

public class BeanScanner {
    private static final Set<Class<? extends Annotation>> beanAnnotations = Set.of(Controller.class, Service.class, Repository.class);

    private Reflections reflections;

    public BeanScanner(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> scanBeanClass() {
        final Set<Class<?>> beans = Sets.newHashSet();

        for(Class<? extends Annotation> annotation: beanAnnotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return beans;
    }
}
