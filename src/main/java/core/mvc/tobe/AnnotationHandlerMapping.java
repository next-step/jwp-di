package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.di.BeanScanner;
import core.di.factory.BeanFactory;
import core.mvc.HandlerMapping;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

@Slf4j
public class AnnotationHandlerMapping implements HandlerMapping {

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );
    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final Class<Controller> HANDLER_ANNOTATION = Controller.class;

    private BeanScanner beanScanner;
    private BeanFactory beanFactory;
    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping() {
        beanScanner = new BeanScanner();
        beanScanner.initialize();
        Set<Class<?>> preInstantiateBeans = beanScanner.scan();

        beanFactory = new BeanFactory(preInstantiateBeans);
        beanFactory.initialize();
    }

    public void initialize() {
        log.info("## Initialized Annotation Handler Mapping");
        Set<Object> controllerInstances = beanFactory.getBeansAnnotatedWith(HANDLER_ANNOTATION);

        Map<HandlerKey, HandlerExecution> handlers = new HashMap<>();
        for (Object controller : controllerInstances) {
            addHandlerExecution(handlers, controller, controller.getClass().getMethods());
        }

        handlerExecutions.putAll(handlers);
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        log.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    private void addHandlerExecution(Map<HandlerKey, HandlerExecution> handlers, final Object target, Method[] methods) {
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .forEach(method -> {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    HandlerKey handlerKey = new HandlerKey(requestMapping.value(), requestMapping.method());
                    HandlerExecution handlerExecution = new HandlerExecution(nameDiscoverer, argumentResolvers, target, method);
                    handlers.put(handlerKey, handlerExecution);
                    log.info("Add - method: {}, path: {}, HandlerExecution: {}", requestMapping.method(), requestMapping.value(), method.getName());
                });
    }

    private HandlerExecution getHandlerInternal(HandlerKey requestHandlerKey) {
        for (Map.Entry<HandlerKey, HandlerExecution> entry : handlerExecutions.entrySet()) {
            if (entry.getKey().isMatch(requestHandlerKey)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
