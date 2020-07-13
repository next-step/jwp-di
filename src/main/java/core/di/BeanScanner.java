package core.di;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();


    public Map<HandlerKey, HandlerExecution> scan(Object... basePackage) {

        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        BeanFactory beanFactory = initializeBeanFactory(basePackage);
        Map<Class<?>,Object> controllers = beanFactory.getControllers();

        for (Class<?> controllerKey : controllers.keySet()) {
            addHandlerExecution(handlers, controllers.get(controllerKey), controllerKey.getMethods());
        }

        return handlers;
    }


    private BeanFactory initializeBeanFactory(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        return new BeanFactory(preInstanticateClazz);
    }


    private void addHandlerExecution(Map<HandlerKey, HandlerExecution> handlers, final Object target, Method[] methods) {
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .forEach(method -> {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    HandlerKey handlerKey = new HandlerKey(requestMapping.value(), requestMapping.method());
                    HandlerExecution handlerExecution = new HandlerExecution(nameDiscoverer, argumentResolvers, target, method);
                    handlers.put(handlerKey, handlerExecution);
                    logger.info("Add - method: {}, path: {}, HandlerExecution: {}", requestMapping.method(), requestMapping.value(), method.getName());
                });
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

}