package core.mvc.tobe;

import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
import core.mvc.tobe.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final BeanFactory beanFactory;

    public BeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();

        for (Map.Entry<Class<?>, Object> controllerEntry : beanFactory.getControllers()) {
            addHandlerExecution(handlers, controllerEntry.getValue(), controllerEntry.getKey().getMethods());
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
