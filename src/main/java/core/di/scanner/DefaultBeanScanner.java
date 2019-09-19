package core.di.scanner;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.bean.BeanDefinition;
import core.di.bean.DefaultBeanDefinition;
import core.di.factory.BeanFactoryUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultBeanScanner implements BeanScanner {

    private static final Class[] ANNOTATION_OF_COMPONENTS = {Controller.class, Service.class, Repository.class};
    private final Reflections reflection;

    public DefaultBeanScanner(Object... basePackage) {
        this.reflection = new Reflections(basePackage);
    }

    public Set<BeanDefinition> scan() {
        Set<Class<?>> annotatedWith = getTypesAnnotatedWith(ANNOTATION_OF_COMPONENTS);

        Set<BeanDefinition> beanDefinitions = annotatedWith.stream()
                .map(clazz -> BeanFactoryUtils.findConcreteClass(clazz, annotatedWith))
                .map(DefaultBeanDefinition::new)
                .collect(Collectors.toSet());

        return beanDefinitions;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflection.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}