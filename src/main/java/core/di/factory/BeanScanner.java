package core.di.factory;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static core.util.ReflectionUtils.newInstance;
import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private Class<? extends Annotation>[] annotations = new Class[] { Controller.class, Service.class, Repository.class, Component.class };

    public BeanScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> getAnnotatedClasses = getAnnotatedClasses(reflections, annotations);

        getAnnotatedClasses.forEach(targetClass -> {
            String name = getComponentName(targetClass);
            ClassBeanDefinition beanDefinition = new ClassBeanDefinition(targetClass, name);
            beanDefinitionRegistry.registerDefinition(beanDefinition);

            logger.info("register {}", beanDefinition);
        });
    }

    private String getComponentName(Class<?> targetClass) {
        Component component = AnnotatedElementUtils.findMergedAnnotation(targetClass, Component.class);
        return "".equals(component.value()) ? targetClass.getName() : component.value();
    }


    private Set<Class<?>> getAnnotatedClasses(Reflections reflections, Class<? extends Annotation> ... annotations) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        for (Class<? extends Annotation> annotation : annotations) {
            classes.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return classes;
    }
}
