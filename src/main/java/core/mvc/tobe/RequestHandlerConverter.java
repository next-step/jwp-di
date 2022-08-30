package core.mvc.tobe;

import core.annotation.web.RequestMapping;
import core.di.factory.ApplicationContext;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class RequestHandlerConverter {

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ApplicationContext context;

    public RequestHandlerConverter(ApplicationContext context) {
        Assert.notNull(context, "'context' must not be null");
        this.context = context;
    }

    public Map<HandlerKey, HandlerExecution> handlers() {
        return context.controllers()
                .stream()
                .flatMap(controller -> handlerKeyExecutions(controller, controller.getClass().getMethods()).entrySet().stream())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<HandlerKey, HandlerExecution> handlerKeyExecutions(Object target, Method[] methods) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(this::handlerKey, method -> handlerExecution(target, method)));
    }

    private HandlerExecution handlerExecution(Object target, Method method) {
        return new HandlerExecution(nameDiscoverer, argumentResolvers, target, method);
    }

    private HandlerKey handlerKey(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        return new HandlerKey(requestMapping.value(), requestMapping.method());
    }
}
