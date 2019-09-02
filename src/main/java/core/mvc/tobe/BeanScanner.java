package core.mvc.tobe;

import com.google.common.collect.ImmutableSet;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class BeanScanner {

    private static final Set<Class<? extends Annotation>> TARGET_ANNOTATIONS = ImmutableSet.of(
            Configuration.class, Controller.class, Service.class, Repository.class
    );

    private final Map<Class<? extends Annotation>, Set<Class<?>>> beanClasses = new HashMap<>();


    public BeanScanner(Class<?> configuration) {
        String[] basePackages = getBasePackages(configuration);
        scanTargetAnnotations(new Reflections(basePackages));
    }

    private String[] getBasePackages(Class<?> configuration) {
        if (configuration.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = configuration.getAnnotation(ComponentScan.class);
            String packageRoot = configuration.getPackage().getName();
            String[] basePackages = componentScan.basePackages();
            if (basePackages.length == 0) {
                return new String[]{packageRoot};
            }
            addPackageRoot(basePackages, packageRoot);
            return basePackages;
        }
        return new String[]{""};
    }

    private void addPackageRoot(String[] basePackages, String packageRoot) {
        for (int i = 0; i < basePackages.length; i++) {
            basePackages[i] = packageRoot + "." + basePackages[i];
        }
    }

    public BeanScanner(Object... basePackage) {
        scanTargetAnnotations(new Reflections(basePackage));
    }

    private void scanTargetAnnotations(Reflections reflections) {
        for (Class<? extends Annotation> annotation : TARGET_ANNOTATIONS) {
            beanClasses.put(annotation, reflections.getTypesAnnotatedWith(annotation));
        }
    }

    public Set<Class<?>> getBeanClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return beanClasses.getOrDefault(annotation, Collections.emptySet());
    }

    public Set<Class<?>> getAllBeanClasses() {
        return beanClasses.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

}
