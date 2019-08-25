package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import java.lang.annotation.Annotation;
import java.util.Set;

public class BeanScanner {

    private BeanFactory beanFactory;
    private Reflections reflections;

    public BeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        this.reflections = new Reflections(basePackage);
    }

    public void enroll(){
        Set<Class<?>> typesAnnotatedWith = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory.initialize(typesAnnotatedWith);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}