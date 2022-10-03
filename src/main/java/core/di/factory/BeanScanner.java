package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.mvc.tobe.support.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    public static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public static BeanFactory scan(Object... basePackage) {
        if (BeanFactory.isScannedPackage(basePackage)) {
            return BeanFactory.getScannedPackage(basePackage);
        }
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> preInstanticateBeans = getTargetAnnotationClass(reflections, Controller.class, Service.class, Repository.class);

        BeanFactory beanFactory = new BeanFactory(preInstanticateBeans);
        beanFactory.initialize();

        BeanFactory.putScannedPackage(beanFactory, basePackage);

        return beanFactory;
    }

    private static Set<Class<?>> getTargetAnnotationClass(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

}
