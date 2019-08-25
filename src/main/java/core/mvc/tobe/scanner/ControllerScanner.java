package core.mvc.tobe.scanner;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import support.exception.ExceptionWrapper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ControllerScanner implements BeanScanner {
    private static final Logger log = LoggerFactory.getLogger(ControllerScanner.class);

    private Reflections reflections;

    public ControllerScanner(Object... basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Map<Class<?>, Object> getBeans() {
        Set<Class<?>> preInitiatedControllers = reflections.getTypesAnnotatedWith(Controller.class);

        if (CollectionUtils.isEmpty(preInitiatedControllers)) {
            return Maps.newHashMap();
        }

        return instantiate(preInitiatedControllers);
    }

    private Map<Class<?>, Object> instantiate(Set<Class<?>> preInitiatedControllers) {
        return preInitiatedControllers.stream()
                .collect(toMap(
                        controller -> controller,
                        ExceptionWrapper.function(Class::newInstance)));
    }

    public Map<HandlerKey, HandlerExecution> createHandlerExecutions() {
        final Map<Class<?>, Object> controllers = getBeans();
        final Set<Method> methods = getRequestMappingMethods(controllers.keySet());

        final Map<HandlerKey, HandlerExecution> handlerExecutions = new HashMap<>();

        methods.forEach(method -> {
            final RequestMapping rm = method.getAnnotation(RequestMapping.class);
            log.debug("register handlerExecution : url is {}, request method : {}, method is {}",
                    rm.value(), rm.method(), method);

            handlerExecutions.put(createHandlerKey(rm),
                    new HandlerExecution(controllers.get(method.getDeclaringClass()), method));
        });

        return handlerExecutions;
    }

    private HandlerKey createHandlerKey(RequestMapping rm) {
        return new HandlerKey(rm.value(), rm.method());
    }

    @SuppressWarnings("unchecked")
    private Set<Method> getRequestMappingMethods(Set<Class<?>> controllers) {
        final Predicate<AnnotatedElement> predicate = ReflectionUtils.withAnnotation(RequestMapping.class)::apply;

        return controllers.stream()
                .map(clazz -> ReflectionUtils.getAllMethods(clazz, predicate::test))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
