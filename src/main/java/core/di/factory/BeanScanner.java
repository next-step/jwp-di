package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class BeanScanner {
    private Reflections reflections;

    public Set<Class<?>> scan(Object... basePackage) {
        reflections = new Reflections(basePackage, new SubTypesScanner(), new TypeAnnotationsScanner());
        return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
    }


    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation, true));
        }
        return beans;
    }
}
