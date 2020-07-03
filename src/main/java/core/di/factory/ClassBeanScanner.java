package core.di.factory;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.util.*;

public class ClassBeanScanner implements BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassBeanScanner.class);

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private Class<? extends Annotation>[] annotations = new Class[] { Controller.class, Service.class, Repository.class, Component.class };

    public ClassBeanScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void scan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> getAnnotatedClasses = ReflectionUtils.getAnnotatedClasses(reflections, annotations);

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
}
