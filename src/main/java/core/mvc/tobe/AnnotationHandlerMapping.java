package core.mvc.tobe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.HandlerMapping;
import core.web.context.WebApplicationContext;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
    private WebApplicationContext webApplicationContext;

    public AnnotationHandlerMapping(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public void initialize() {
        Set<Class<?>> controllers = getControllers();

        Set<Method> methods = getRequestMappingMethods(controllers);
        for (Method method : methods) {
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            logger.debug("register handlerExecution : url is {}, request method : {}, method is {}",
                    rm.value(), rm.method(), method);
            handlerExecutions.put(createHandlerKey(rm),
                    new HandlerExecution(webApplicationContext.getBean(method.getDeclaringClass()), method));
        }

        logger.info("Initialized AnnotationHandlerMapping!");
    }

    private Set<Class<?>> getControllers() {
        return webApplicationContext.getBeans().entrySet().stream()
                .filter(entry -> entry.getKey().getAnnotation(Controller.class) != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private HandlerKey createHandlerKey(RequestMapping rm) {
        return new HandlerKey(rm.value(), rm.method());
    }

    @SuppressWarnings("unchecked")
    private Set<Method> getRequestMappingMethods(Set<Class<?>> controlleers) {
        Set<Method> requestMappingMethods = Sets.newHashSet();
        for (Class<?> clazz : controlleers) {
            requestMappingMethods
                    .addAll(ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(RequestMapping.class)));
        }
        return requestMappingMethods;
    }


    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return handlerExecutions.get(new HandlerKey(requestUri, rm));
    }
}
