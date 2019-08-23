package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

    private Reflections reflections;
    private BeanFactory beanFactory;

    public BeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        reflections = new Reflections(basePackage);
    }

    public void scan() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Set<Class<?>> preInitiatedControllers = getTypesAnnotatedWith(Controller.class, Repository.class, Service.class);
        for (Class<?> preInitiatedController : preInitiatedControllers) {
            beanFactory.initializeBean(preInitiatedController);
        }
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
