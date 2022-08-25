package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.config.WebMvcConfiguration;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClasspathBeanScanner {
    private final BeanFactory beanFactory;
    private Reflections reflections;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void scan(Class<? extends WebMvcConfiguration> configurationClazz) {
        if (!configurationClazz.isAnnotationPresent(ComponentScan.class)) {
            return;
        }

        ComponentScan annotation = configurationClazz.getAnnotation(ComponentScan.class);
        for (String path : annotation.basePackages()) {
            scan(path);
        }
    }

    @SuppressWarnings("unchecked")
    public void scan(String path) {
        reflections = new Reflections(path);
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory.register(preInstanticateClazz);
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
