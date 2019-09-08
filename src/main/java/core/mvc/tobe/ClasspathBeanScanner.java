package core.mvc.tobe;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClasspathBeanScanner {
    private static final Logger log = LoggerFactory.getLogger(ClasspathBeanScanner.class);

    private Reflections reflections;
    private BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public void doScan(Object... basePackage) {
        reflections = new Reflections(basePackage);
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
        for (Class<?> beanClass : preInstanticateClazz) {
            beanDefinitions.put(beanClass, new DefaultBeanDefinition(beanClass));
        }

        beanFactory.register(beanDefinitions);
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
