package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigurationBeanScanner {
    private final BeanFactory beanFactory;
    private final Map<Class<?>, Method> methods = Maps.newHashMap();
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        final Object instance = BeanUtils.instantiateClass(clazz);

        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> methods.put(method.getReturnType(), method));

        methods.forEach((key, value) -> beans.put(key, instanticateBean(value, instance)));
        beanFactory.addBean(beans);
    }

    private Object instanticateBean(Method method, Object instance) {
        if (beans.containsKey(method.getReturnType())) {
            return beans.get(method.getReturnType());
        }
        return instanticate(method, instance);
    }

    private Object instanticate(Method method, Object instance) {
        if (method.getParameterCount() == 0) {
            return ReflectionUtils.invokeMethod(method, instance);
        }
        return ReflectionUtils.invokeMethod(method, instance, getArgs(method, instance));
    }

    private Object[] getArgs(Method method, Object instance) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final List<Object> args = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            final Object parameterInstance = getInstance(parameterType, instance);
            args.add(parameterInstance);
            beans.put(parameterType, parameterInstance);
        }
        return args.toArray();
    }

    private Object getInstance(Class<?> parameterType, Object instance) {
        if (beans.containsKey(parameterType)) {
            return beans.get(parameterType);
        }
        return instanticateBean(methods.get(parameterType), instance);
    }
}
