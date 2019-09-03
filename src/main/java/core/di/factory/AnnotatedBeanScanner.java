package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.bean.BeanDefinition;
import core.di.bean.DefaultBeanDefinition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public class AnnotatedBeanScanner implements BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(AnnotatedBeanScanner.class);
    private static final Class[] BEAN_ANNOTATION_CLASS = {Controller.class, Service.class, Repository.class};


    private BeanFactory beanFactory;
    private Reflections reflections;


    public AnnotatedBeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        reflections = new Reflections(basePackage);
    }

    public void scan() {
        Set<Class<?>> beanClasses = getTypesAnnotatedWith(BEAN_ANNOTATION_CLASS);
        for (Class<?> beanClass : beanClasses) {
            BeanDefinition beanDefinition = new DefaultBeanDefinition(beanClass);
            beanFactory.registerBeanDefinition(beanDefinition);
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
