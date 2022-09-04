package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import core.mvc.tobe.support.*;
import core.util.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private Object[] basePackage;

    public BeanScanner() {
    }

    public BeanScanner(final Object... basePackage) {
        this.basePackage = basePackage;
    }

    private static final List<ArgumentResolver> argumentResolvers = asList(
                new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver()
        );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public Map<HandlerKey, HandlerExecution> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        final Set<Class<?>> preInstanticateBeans = ReflectionUtils.getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);
        final BeanFactory beanFactory = new BeanFactory(preInstanticateBeans);
        beanFactory.initialize();

        Set<Class<?>> controllerTypes = beanFactory.getControllerTypes();

        return addHandlerExecution(beanFactory, controllerTypes);
    }

    public void scan(final BeanFactory beanFactory) {
        Reflections reflections = new Reflections(basePackage);

        final Set<Class<?>> preInstanticateBeans = ReflectionUtils.getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        beanFactory.addPreInstanticateBeans(preInstanticateBeans.toArray(Class<?>[]::new));
        beanFactory.initialize();
    }

    public Map<HandlerKey, HandlerExecution> getHandlerExecutions(final BeanFactory beanFactory) {
        Set<Class<?>> controllerTypes = beanFactory.getControllerTypes();

        return addHandlerExecution(beanFactory, controllerTypes);
    }

    private Map<HandlerKey, HandlerExecution> addHandlerExecution(final BeanFactory beanFactory, final Set<Class<?>> controllerTypes) {
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        for (Class<?> controllerType : controllerTypes) {
            Object target = beanFactory.getBean(controllerType);
            addHandlerExecution(handlers, target, controllerType.getMethods());
        }

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

}
