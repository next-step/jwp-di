package core.mvc.tobe;

import core.annotation.web.RequestMapping;
import core.mvc.tobe.support.*;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHandlerConverter {
    private static final List<ArgumentResolver> argumentResolvers = List.of(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final Collection<Object> controllers;

    public RequestHandlerConverter(Collection<Object> controllers) {
        Assert.notNull(controllers, "controllers가 null이어선 안됩니다.");
        this.controllers = controllers;
    }

    public Map<HandlerKey, HandlerExecution> handlers() {
        return controllers.stream()
                .flatMap(controller -> handlerKeyExecutions(controller, controller.getClass().getMethods())
                        .entrySet().stream())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<HandlerKey, HandlerExecution> handlerKeyExecutions(Object target, Method[] methods) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(this::handlerKey, method -> handlerExecution(target, method)));
    }

    private HandlerKey handlerKey(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        return new HandlerKey(requestMapping.value(), requestMapping.method());
    }

    private HandlerExecution handlerExecution(Object target, Method method) {
        return new HandlerExecution(nameDiscoverer, argumentResolvers, target, method);
    }
}
