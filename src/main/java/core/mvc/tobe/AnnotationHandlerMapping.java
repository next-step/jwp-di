package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.di.factory.BeanFactory;
import core.mvc.HandlerMapping;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class AnnotationHandlerMapping implements HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final ArgumentResolverComposite argumentResolverComposite = new ArgumentResolverComposite();

    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
    private final BeanFactory beanFactory;

    public AnnotationHandlerMapping(Object... basePackage) {
        beanFactory = new BeanFactory(basePackage);
        beanFactory.initialize(Controller.class);
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        Map<Class<?>, Object> controllers = beanFactory.getBeansAnnotatedWith(Controller.class);
        for (Entry<Class<?>, Object> controllerEntry : controllers.entrySet()) {
            addHandlerExecution(handlerExecutions, controllerEntry.getValue(), controllerEntry.getKey().getMethods());
        }
    }

    private void addHandlerExecution(Map<HandlerKey, HandlerExecution> handlers, final Object target, Method[] methods) {
        Arrays.stream(methods)
              .filter(method -> method.isAnnotationPresent(RequestMapping.class))
              .forEach(method -> {
                  RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                  HandlerKey handlerKey = new HandlerKey(requestMapping.value(), requestMapping.method());
                  HandlerExecution handlerExecution = new HandlerExecution(nameDiscoverer, argumentResolverComposite, target, method);
                  handlers.put(handlerKey, handlerExecution);
                  logger.info("Add - method: {}, path: {}, HandlerExecution: {}", requestMapping.method(), requestMapping.value(), method.getName());
              });
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    private HandlerExecution getHandlerInternal(HandlerKey requestHandlerKey) {
        for (Entry<HandlerKey, HandlerExecution> handlerExecutionEntry : handlerExecutions.entrySet()) {
            if (handlerExecutionEntry.getKey().isMatch(requestHandlerKey)) {
                return handlerExecutionEntry.getValue();
            }
        }

        return null;
    }
}
