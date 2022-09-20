package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final BeanScanner beanScanner = new BeanScanner();
    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public static BeanScanner getInstance() {
        return beanScanner;
    }

    public Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.MethodsAnnotated);
        Set<Class<? extends Annotation>> targetAnnotations = new HashSet<>(Arrays.asList(Controller.class, Service.class, Repository.class));

        return this.scanBeanClassesWithAnnotations(reflections, targetAnnotations);
    }

    public Map<HandlerKey, HandlerExecution> getHandlers(Set<Object> classes) {

        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();

        Set<Object> controllers = classes.stream()
                .filter(this::isController)
                .collect(Collectors.toSet());

        for (Object controller : controllers) {
            addHandlerExecution(handlers, controller, controller.getClass().getMethods());
        }

        return handlers;
    }

    private boolean isController(Object clazz) {
        return Arrays.stream(clazz.getClass().getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().equals(Controller.class));
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

    private Set<Class<?>> scanBeanClassesWithAnnotations(Reflections reflections, Set<Class<? extends Annotation>> targetAnnotations) {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<? extends Annotation> targetAnnotation : targetAnnotations) {
            Set<Class<?>> targetBeanClasses = reflections.getTypesAnnotatedWith(targetAnnotation);
            classes.addAll(targetBeanClasses);
        }

        return classes;
    }

}
