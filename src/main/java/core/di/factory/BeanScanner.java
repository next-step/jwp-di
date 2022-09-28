package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static core.util.ReflectionUtils.newInstance;
import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final Set<Class<? extends Annotation>> beanAnnotations = Set.of(Controller.class, Service.class, Repository.class);

    private Reflections reflections;

    public BeanScanner(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> scanBeanClass() {
        final Set<Class<?>> beans = Sets.newHashSet();

        for(Class<? extends Annotation> annotation: beanAnnotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return beans;
    }
}
