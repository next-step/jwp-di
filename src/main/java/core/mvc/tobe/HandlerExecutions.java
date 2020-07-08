package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMapping;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Created by iltaek on 2020/07/08 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class HandlerExecutions {

    private static final Logger logger = LoggerFactory.getLogger(HandlerExecutions.class);

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final ArgumentResolverComposite argumentResolverComposite = new ArgumentResolverComposite();

    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public HandlerExecutions() {
    }

    public void addHandlerExecution(final Object target, Method[] methods) {
        Arrays.stream(methods)
              .filter(method -> method.isAnnotationPresent(RequestMapping.class))
              .forEach(method -> addHandlerExecution(target, method));
    }

    private void addHandlerExecution(Object target, Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

        handlerExecutions.put(new HandlerKey(requestMapping.value(), requestMapping.method()),
                              new HandlerExecution(nameDiscoverer, argumentResolverComposite, target, method));
        logger.info("Add - method: {}, path: {}, HandlerExecution: {}", requestMapping.method(), requestMapping.value(), method.getName());
    }

    public Object getHandler(HandlerKey requestHandlerKey) {
        return handlerExecutions.entrySet().stream()
                                .filter(handlerExecutionEntry -> handlerExecutionEntry.getKey().isMatch(requestHandlerKey))
                                .findFirst()
                                .map(Entry::getValue)
                                .orElse(null);
    }
}
