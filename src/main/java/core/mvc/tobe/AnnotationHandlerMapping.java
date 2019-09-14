package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.di.factory.BeanFactory;
import core.mvc.HandlerMapping;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private Object[] basePackage;

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public void initialize() {
        final AnnotationScanner beanScanner = new AnnotationScanner(basePackage);
        final ConfigurationScanner configurationScanner = new ConfigurationScanner(basePackage);

        final BeanFactory beanFactory = new BeanFactory(beanScanner, configurationScanner);
        beanFactory.initialize();

        handlerExecutions.putAll(createHandlerExecutions(beanFactory));

        logger.info("Initialized AnnotationHandlerMapping!");
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return handlerExecutions.get(new HandlerKey(requestUri, rm));
    }

    private Map<HandlerKey, HandlerExecution> createHandlerExecutions(final BeanFactory beanFactory) {
        final Set<Method> methods = getRequestMappingMethods(beanFactory.getControllers());
        return methods.stream()
                .collect(toMap(
                        this::createHandlerKey,
                        method -> createHandlerExecution(beanFactory, method)));
    }

    @SuppressWarnings("unchecked")
    private Set<Method> getRequestMappingMethods(Set<Class<?>> controllers) {
        final Predicate<AnnotatedElement> predicate = ReflectionUtils.withAnnotation(RequestMapping.class)::apply;

        return controllers.stream()
                .map(clazz -> ReflectionUtils.getAllMethods(clazz, predicate::test))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private HandlerKey createHandlerKey(final Method method) {
        final RequestMapping rm = method.getAnnotation(RequestMapping.class);
        logger.debug("register handlerExecution : url is {}, request method : {}, method is {}",
                rm.value(), rm.method(), method);

        return new HandlerKey(rm.value(), rm.method());
    }

    private HandlerExecution createHandlerExecution(final BeanFactory beanFactory, final Method method) {
        return new HandlerExecution(beanFactory.getBean(method.getDeclaringClass()), method);
    }
}
