package core.di.factory.scanner;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;

import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.bean.BeanInfo;
import core.di.factory.bean.BeanInvokeType;
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
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created By kjs4395 on 2020-07-20
 */
public class ClasspathBeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathBeanScanner.class);

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(Object...basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        beanFactory.register(getBeanInfos(preInstanticateClazz));
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        Set<Object> controllers = this.beanFactory.controllers();
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();

        for (Object controller : controllers) {
            addHandlerExecution(handlers, controller, controller.getClass().getMethods());
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

    private Set<BeanInfo> getBeanInfos(Set<Class<?>> preInstanticateClazz) {
        return preInstanticateClazz.stream()
                .map(clazz -> {
                    return new BeanInfo(clazz, clazz, BeanInvokeType.CONSTRUCTOR,
                            BeanFactoryUtils.getInjectedConstructor(clazz), null);})
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
