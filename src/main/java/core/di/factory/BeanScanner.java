package core.di.factory;

import com.google.common.collect.ImmutableSet;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final List<ArgumentResolver> argumentResolvers = Arrays.asList(
        new HttpRequestArgumentResolver(),
        new HttpResponseArgumentResolver(),
        new RequestParamArgumentResolver(),
        new PathVariableArgumentResolver(),
        new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public Map<HandlerKey, HandlerExecution> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(),
            new MethodAnnotationsScanner());
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();

        Set<Class<?>> beans = findBeanClasses(reflections);
        var beanFactory = new BeanFactory(beans);
        beanFactory.initialize();

        for (Class<?> bean : beans) {
            if (!bean.isAnnotationPresent(Controller.class)) {
                continue;
            }

            Object target = beanFactory.getBean(bean);
            addHandlerExecution(handlers, target, bean.getMethods());
        }

        return handlers;
    }

    private Set<Class<?>> findBeanClasses(Reflections reflections) {
        return Stream.of(Controller.class, Service.class, Repository.class)
            .flatMap(it -> reflections.getTypesAnnotatedWith(it).stream())
            .collect(Collectors.toSet());
    }

    private void addHandlerExecution(Map<HandlerKey, HandlerExecution> handlers, final Object target,
        Method[] methods) {
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

}
