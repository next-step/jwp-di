package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final BeanScanner beanScanner = new BeanScanner();

    public static BeanScanner getInstance() {
        return beanScanner;
    }

    public Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.MethodsAnnotated);
        Set<Class<? extends Annotation>> targetAnnotations = new HashSet<>(Arrays.asList(Controller.class, Service.class, Repository.class));

        return this.scanBeanClassesWithAnnotations(reflections, targetAnnotations);
    }


    private Set<Class<?>> scanBeanClassesWithAnnotations(Reflections reflections, Set<Class<? extends Annotation>> targetAnnotations) {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<? extends Annotation> targetAnnotation : targetAnnotations) {
            Set<Class<?>> targetBeanClasses = reflections.getTypesAnnotatedWith(targetAnnotation);
            classes.addAll(targetBeanClasses);
        }

        return classes;
    }

}
