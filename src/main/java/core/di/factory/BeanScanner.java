package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(BeanScanner.class);

    private Reflections reflections;

    public BeanScanner(Object... basePackage) {
        if (basePackage.length == 0) {
            throw new IllegalArgumentException("BasePackage is empty");
        }
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> getPreInitiatedClasses() {
        return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    public void scan(BeanFactory beanFactory) {
        Set<BeanDefinition> beanDefs = Sets.newHashSet();
        for (Class<?> preInitiatedClass : getPreInitiatedClasses()) {
            beanDefs.add(new ComponentBeanDefinition(preInitiatedClass));
        }
        beanFactory.addBeanDefs(beanDefs);
    }
}
