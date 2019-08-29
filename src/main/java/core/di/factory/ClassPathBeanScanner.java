package core.di.factory;

import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.config.BeanDefinition;
import core.di.factory.config.ClassBeanDefinition;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathBeanScanner {

    private final List<Class<? extends Annotation>> TARGET_ANNOTATIONS = Arrays.asList(Controller.class
            , Service.class
            , Repository.class);

    private final Reflections reflections;

    public ClassPathBeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> getPreInstanticateClasses() {
        return TARGET_ANNOTATIONS.stream()
                .map(this::getClassesByAnnotation)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<BeanDefinition> getBeanDefinitions(){
        return this.getPreInstanticateClasses().stream()
                .map(ClassBeanDefinition::new)
                .collect(Collectors.toSet());
    }

    public Set<Class<?>> getConfigurationClasses() {
        return getClassesByAnnotation(Configuration.class);
    }

    private Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }


}
