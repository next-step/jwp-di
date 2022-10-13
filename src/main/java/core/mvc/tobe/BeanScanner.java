package core.mvc.tobe;

import static java.util.Arrays.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;
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
        var beanFactory = new BeanFactory();

        var classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(basePackage);

        var configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(basePackage);

        beanFactory.initialize();


        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        for (Class<?> bean : beanFactory.getBeans()) {
            if (!bean.isAnnotationPresent(Controller.class)) {
                continue;
            }

            Object target = beanFactory.getBean(bean);
            addHandlerExecution(handlers, target, bean.getMethods());
        }

        return handlers;
    }

    private Set<Class<?>> findBeanClasses(Reflections reflections, Class<? extends Annotation>... classes) {
        return Arrays.stream(classes)
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
