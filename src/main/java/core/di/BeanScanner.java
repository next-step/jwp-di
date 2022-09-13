package core.di;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.*;
import org.reflections.Reflections;
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
    private static final List<Class<? extends Annotation>> ANNOTATIONS = List.of(Controller.class, Repository.class, Service.class);

    private static final List<ArgumentResolver> argumentResolvers = asList(
                new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver()
        );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final Object[] basePackage;
    private final BeanFactory beanFactory;

    public BeanScanner(BeanFactory beanFactory, Object[] basePackage) {
        this.basePackage = basePackage;
        this.beanFactory = beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        Set<Class<?>> beans = beans();
        beans.forEach(bean -> addHandlerExecution(handlers, newInstance(bean), bean.getMethods()));
        this.beanFactory.addPreInstanticateBeans(beans);
        return handlers;
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

    private Set<Class<?>> beans() {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> set = new HashSet<>();
        ANNOTATIONS.forEach(annotation -> set.addAll(reflections.getTypesAnnotatedWith(annotation)));
        return set;
    }

}
