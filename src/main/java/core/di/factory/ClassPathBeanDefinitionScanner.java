package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.config.DefaultBeanDefinition;
import core.di.factory.support.BeanDefinitionRegistry;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassPathBeanDefinitionScanner {
    private static final Logger log = LoggerFactory.getLogger(ClassPathBeanDefinitionScanner.class);

    private Reflections reflections;
    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Set<Class<?>> getBeanClasses() {
        Set<Class<?>> preInitiatedControllers = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        return preInitiatedControllers;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    public void scan(Object... basePackages) {
        reflections = new Reflections(basePackages);
        Set<Class<?>> beanClasses = getBeanClasses();
        beanClasses.stream().forEach(clazz -> registry.registerBeanDefinition(new DefaultBeanDefinition(clazz)));
    }
}
