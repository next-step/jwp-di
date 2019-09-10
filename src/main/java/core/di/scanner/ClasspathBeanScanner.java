package core.di.scanner;

import com.google.common.collect.ImmutableSet;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ClasspathBeanScanner {

    private final Map<Class<? extends Annotation>, Set<Class<?>>> beanClasses = new HashMap<>();
    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(String... basePackage) {
        scanTypeClasses(new Reflections(basePackage));
        beanFactory.addAllBeanClasses(getAllBeanClasses());
    }

    private void scanTypeClasses(Reflections reflections) {
        for (Class<? extends Annotation> type : ScannableAnnotaionTypes.getAllTypes()) {
            Set<Class<?>> classes = beanClasses.getOrDefault(type, new HashSet<>());
            classes.addAll(reflections.getTypesAnnotatedWith(type));
            beanClasses.put(type, classes);
        }
    }

    private Set<Class<?>> getAllBeanClasses() {
        return this.beanClasses.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<Class<?>> getBeanClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return beanClasses.getOrDefault(annotation, new HashSet<>());
    }
}
