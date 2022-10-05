package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMethod;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.mvc.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final Object[] basePackage;
    private final ClasspathBeanScanner classpathBeanScanner;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
        classpathBeanScanner = new ClasspathBeanScanner(new BeanFactory());
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        classpathBeanScanner.doScan(basePackage);
        Map<Class<?>, Object> controllers = classpathBeanScanner.getFactoryController();

        ControllerResolver controllerResolver = new ControllerResolver();
        for (Map.Entry<Class<?>, Object> classObjectEntry : controllers.entrySet()) {
            controllerResolver.addHandlerExecution(handlerExecutions, controllers.get(classObjectEntry.getKey()),
                    classObjectEntry.getKey().getDeclaredMethods());
        }
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
}
