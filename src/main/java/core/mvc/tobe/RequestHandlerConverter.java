package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public final class RequestHandlerConverter {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();
    private static final Class<Controller> CONTROLLER_ANNOTATION = Controller.class;
    private static final List<ArgumentResolver> ARGUMENT_RESOLVERS = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private final Collection<Object> controllers;

    public RequestHandlerConverter(Collection<Object> controllers) {
        Assert.notNull(controllers, "'controllers' must not be null");
        Assert.isTrue(isControllers(controllers), String.format("controllers(%s) must be annotated with %s", controllers, CONTROLLER_ANNOTATION));
        this.controllers = controllers;
    }

    public Map<HandlerKey, HandlerExecution> handlers() {
        return controllers.stream()
                .flatMap(controller -> handlerKeyExecutions(controller, controller.getClass().getMethods()).entrySet().stream())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isControllers(Collection<Object> controllers) {
        return controllers.stream()
                .allMatch(controller -> controller != null && controller.getClass().isAnnotationPresent(CONTROLLER_ANNOTATION));
    }

    private Map<HandlerKey, HandlerExecution> handlerKeyExecutions(Object target, Method[] methods) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(this::handlerKey, method -> handlerExecution(target, method)));
    }

    private HandlerExecution handlerExecution(Object target, Method method) {
        return new HandlerExecution(NAME_DISCOVERER, ARGUMENT_RESOLVERS, target, method);
    }

    private HandlerKey handlerKey(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        return new HandlerKey(requestMapping.value(), requestMapping.method());
    }
}
