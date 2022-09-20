package core.mvc.tobe;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.common.collect.Maps;

import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.di.ApplicationContext;
import core.mvc.HandlerMapping;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    private final ApplicationContext applicationContext;

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver());

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public AnnotationHandlerMapping(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        handlerExecutions.putAll(createHandlerExecutions());
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    private HandlerExecution getHandlerInternal(HandlerKey requestHandlerKey) {
        for (HandlerKey handlerKey : handlerExecutions.keySet()) {
            if (handlerKey.isMatch(requestHandlerKey)) {
                return handlerExecutions.get(handlerKey);
            }
        }
        return null;
    }

    private Map<HandlerKey, HandlerExecution> createHandlerExecutions() {
        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        Map<Class<?>, Object> controllers = applicationContext.getBeanFactory().getControllers();
        for (Class<?> controller : controllers.keySet()) {
            Object target = controllers.get(controller);
            addHandlerExecution(handlers, target, controller.getMethods());
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
