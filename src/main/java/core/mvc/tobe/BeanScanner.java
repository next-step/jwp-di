package core.mvc.tobe;

import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.common.collect.Sets;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;

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
        Set<Class<?>> preInstantiateBeans = getPreInstantiateBeans(basePackage);
        BeanFactory beanFactory = new BeanFactory(preInstantiateBeans);
        beanFactory.initialize();

        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        Map<Class<?>, Object> controllers = beanFactory.getControllers();
        controllers.forEach((clazz, handler) -> addHandlerExecution(handlers, handler, clazz.getMethods()));

        return handlers;
    }

    private static Set<Class<?>> getPreInstantiateBeans(Object[] basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        List<Class<? extends Annotation>> annotationClasses = List.of(Controller.class, Service.class, Repository.class);
        Set<Class<?>> preInstantiateBeans = Sets.newHashSet();
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            preInstantiateBeans.addAll(reflections.getTypesAnnotatedWith(annotationClass));
        }
        return preInstantiateBeans;
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

}
