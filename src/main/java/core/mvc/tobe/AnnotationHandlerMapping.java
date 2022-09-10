package core.mvc.tobe;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import core.annotation.web.RequestMethod;
import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.mvc.HandlerMapping;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final Object[] basePackage;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");

        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(reflections, Controller.class);
        BeanFactory beanFactory = new BeanFactory(preInstanticateClazz);
        beanFactory.initialize();

        BeanScanner beanScanner = new BeanScanner(beanFactory);

        handlerExecutions.putAll(beanScanner.scan(Controller.class, basePackage));
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
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
