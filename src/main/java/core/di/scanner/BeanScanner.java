package core.di.scanner;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public class BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

    private Reflections reflections;
    private BeanFactory beanFactory;

    public BeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        reflections = new Reflections(basePackage);
    }

    public void initialize() {
        Set<Class<?>> preInstanticateBeans = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        preInstanticateBeans.forEach(targetBean -> beanFactory.registerBean(targetBean));
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
