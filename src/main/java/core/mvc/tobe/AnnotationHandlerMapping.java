package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;
import core.mvc.HandlerMapping;
import core.mvc.tobe.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private Object[] basePackage;
    private ConfigurationBeanScanner configurationBeanScanner;
    private ClasspathBeanScanner beanScanner;
    private BeanFactory beanFactory;

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private static final List<ArgumentResolver> argumentResolvers = asList(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
    );

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
        beanScanner = new ClasspathBeanScanner();
        configurationBeanScanner = new ConfigurationBeanScanner();
        beanFactory = new BeanFactory(beanScanner.scan(basePackage));
        beanFactory.instantiateConfiguration(configurationBeanScanner.scan());
        beanFactory.initialize();
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        Map<Class<?>, Object> controllerBeans = beanFactory.getControllers();

        for (Class<?> controller : controllerBeans.keySet()) {
            Object target = controllerBeans.get(controller);
            addHandlerExecution(handlerExecutions, target, controller.getMethods());
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
