package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.di.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedBeanDefinition {
    private static final Logger logger = LoggerFactory.getLogger(AnnotatedBeanDefinition.class);

    private BeanFactory beanFactory;
    private Map<Class<?>, Method> beanMethods = Maps.newHashMap();

    public AnnotatedBeanDefinition(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void registerBean(Class<?>[] annotatedClasses) {
        for (Class<?> configClass : annotatedClasses) {
            scanConfigClass(configClass);
        }
    }

    private void scanConfigClass(Class<?> configClass) {
        Map<Class<?>, Method> beanMethod = getBeanTypes(configClass);
        beanMethods.putAll(beanMethod);
        for (Class<?> clazz : beanMethod.keySet()) {
            addBean(configClass, clazz);
        }
    }

    private Map<Class<?>, Method> getBeanTypes(Class<?> annotatedClasses) {
        return Stream.of(annotatedClasses)
                .map(Class::getDeclaredMethods)
                .flatMap(Stream::of)
                .filter(methods -> methods.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(Method::getReturnType, method -> method));
    }

    private void addBean(Class<?> configClass, Class<?> clazz) {
        if (beanFactory.containsBean(clazz)) {
            return;
        }

        instantiateBean(configClass, clazz);
    }

    private Object instantiateBean(Class<?> configClass, Class<?> clazz) {
        Method method = beanMethods.get(clazz);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = instantiateBean(configClass, parameterTypes[i]);
        }

        Object object = getBean(configClass, method, parameters);
        beanFactory.addBean(clazz, object);
        return object;
    }

    private Object getBean(Class<?> configClass, Method method, Object[] parameters) {
        try {
            return method.invoke(configClass.newInstance(), parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage());
            throw new CreateInstanceFailException();
        }
    }
}
