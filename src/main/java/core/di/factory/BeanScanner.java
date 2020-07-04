package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private Set<Class<?>> preInstanticateBeans;
    private BeanFactory beanFactory;

    public BeanScanner(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        this.preInstanticateBeans = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);
        this.beanFactory = new BeanFactory(preInstanticateBeans);
    }

    public Map<Class<?>, Object> getBeansAnnotationWith(Class<? extends Annotation> annotationClass) {
        Map<Class<?>, Object> beans = new HashMap<>();
        for (Class<?> bean : preInstanticateBeans) {
            if (bean.isAnnotationPresent(annotationClass)) {
                beans.put(bean, beanFactory.getBean(bean));
            }
        }
        return beans;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
