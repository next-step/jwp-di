package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.di.factory.BeanFactory;
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

public class RequestHandlerScanner {

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final BeanFactory beanFactory;

    public RequestHandlerScanner(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "'beanFactory' must not be null");
        this.beanFactory = beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        return beanFactory.annotatedWith(Controller.class)
                .entrySet()
                .stream()
                .flatMap(entry -> handlerKeyExecutions(entry.getValue(), entry.getKey().getMethods()).entrySet().stream())
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
