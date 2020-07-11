package core.mvc.tobe;

import static java.util.Arrays.asList;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.context.ApplicationContext;
import core.mvc.HandlerMapping;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class AnnotationHandlerMapping implements HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private static final List<ArgumentResolver> argumentResolvers = asList(
        new HttpRequestArgumentResolver(),
        new HttpResponseArgumentResolver(),
        new RequestParamArgumentResolver(),
        new PathVariableArgumentResolver(),
        new ModelArgumentResolver()
    );
    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
    private final Object[] controllers;

    public AnnotationHandlerMapping(Object[] controllers) {
        this.controllers = controllers;
    }



    public void initialize() {
        for(Object controller : controllers){
            addHandlerExecutions(controller);
        }
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    private HandlerExecution getHandlerInternal(HandlerKey requestHandlerKey) {
        for (HandlerKey handlerKey : this.handlerExecutions.keySet()) {
            if (handlerKey.isMatch(requestHandlerKey)) {
                return this.handlerExecutions.get(handlerKey);
            }
        }

        return null;
    }

    private void addHandlerExecutions(Object controller) {
        Method[] methods = controller.getClass().getDeclaredMethods();
        Arrays.stream(methods)
            .filter(method -> method.isAnnotationPresent(RequestMapping.class))
            .forEach(method -> {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                HandlerKey handlerKey = new HandlerKey(requestMapping.value(), requestMapping.method());
                HandlerExecution handlerExecution = new HandlerExecution(this.nameDiscoverer, this.argumentResolvers, controller, method);
                this.handlerExecutions.put(handlerKey, handlerExecution);
                logger.info("Add - method: {}, path: {}, HandlerExecution: {}", requestMapping.method(), requestMapping.value(), method.getName());
            });
    }
}
