package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.support.ArgumentResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RequestHandlerConverter {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();
    private static final Class<Controller> CONTROLLER_ANNOTATION = Controller.class;

    private final List<ArgumentResolver> argumentResolvers;

    public RequestHandlerConverter(List<ArgumentResolver> argumentResolvers) {
        Assert.notNull(argumentResolvers, "'argumentResolvers' must not be null");
        Assert.noNullElements(argumentResolvers, "'argumentResolvers' must not contains null");
        this.argumentResolvers = Collections.unmodifiableList(argumentResolvers);
    }

    public Map<HandlerKey, HandlerExecution> handlers(Collection<Object> controllers) {
        Assert.notNull(controllers, "'controllers' must not be null");
        Assert.isTrue(isControllers(controllers), String.format("controllers(%s) must be annotated with %s", controllers, CONTROLLER_ANNOTATION));
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
        return new HandlerExecution(NAME_DISCOVERER, argumentResolvers, target, method);
    }

    private HandlerKey handlerKey(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        return new HandlerKey(requestMapping.value(), requestMapping.method());
    }
}
