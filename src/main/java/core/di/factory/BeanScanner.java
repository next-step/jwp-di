package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

    private Reflections reflections;
    private BeanFactory beanFactory;

    public BeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
        this.init();
    }

    private void init() {
        final Set<Class<?>> beanTypes = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        this.beanFactory = new BeanFactory(beanTypes);
        this.beanFactory.initialize();
    }

    public Map<Class<?>, Object> getControllers() {
        return this.beanFactory.getBeansAnnotatedWith(Controller.class);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
