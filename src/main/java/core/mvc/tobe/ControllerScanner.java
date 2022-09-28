package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
import core.mvc.tobe.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ControllerScanner {

    private static final Logger logger = LoggerFactory.getLogger(core.mvc.tobe.ControllerScanner.class);

    private static final List<ArgumentResolver> argumentResolvers = asList(
                new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver()
        );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final BeanFactory beanFactory;

    public ControllerScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        return beanFactory.annotatedWith(Controller.class)
                .entrySet()
                .stream()
                .flatMap(entry -> getHandlerKeyExecutions(entry.getValue(), entry.getKey().getMethods()).entrySet().stream())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<HandlerKey, HandlerExecution> getHandlerKeyExecutions(Object target, Method[] methods) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(this::getHandlerKey, method -> getHandlerExecution(target, method)));
    }

    private HandlerExecution getHandlerExecution(Object target, Method method) {
        return new HandlerExecution(nameDiscoverer, argumentResolvers, target, method);
    }

    private HandlerKey getHandlerKey(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        return new HandlerKey(requestMapping.value(), requestMapping.method());
    }

}
