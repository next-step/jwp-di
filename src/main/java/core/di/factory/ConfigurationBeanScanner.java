package core.di.factory;

import core.annotation.*;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configClass) {
        Object configurationClass = BeanUtils.instantiateClass(configClass);
        Method[] declaredMethods = configClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Bean annotatedBean = declaredMethod.getAnnotation(Bean.class);
            if (annotatedBean == null) {
                continue;
            }

            Object object = getObject(configurationClass, declaredMethods, declaredMethod);
            beanFactory.setBean(declaredMethod.getReturnType(), Optional.ofNullable(object).orElseThrow(IllegalArgumentException::new));
        }
    }

    private Object getObject(Object configurationClass, Method[] declaredMethods, Method declaredMethod) {
        int parameterCount = declaredMethod.getParameterCount();
        if (parameterCount == 0) {
            return invoke(declaredMethod, configurationClass);
        }

        List<Object> objects = new ArrayList<>();
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            getParameters(configurationClass, declaredMethods, objects, parameterType);
        }
        return invoke(declaredMethod, configurationClass, objects.toArray());
    }

    private void getParameters(Object configurationClass, Method[] declaredMethods, List<Object> objects, Class<?> parameterType) {
        Object parameter = beanFactory.getBean(parameterType);
        if (parameter != null) {
            objects.add(parameter);
            return;
        }

        for (Method method : declaredMethods) {
            if (method.getName().equalsIgnoreCase(parameterType.getSimpleName())) {
                Object object = getObject(configurationClass, declaredMethods, method);
                objects.add(object);
                beanFactory.setBean(method.getReturnType(), Optional.ofNullable(object).orElseThrow(IllegalArgumentException::new));
            }
        }
    }

    private Object invoke(Method method, Object configurationClass, Object... args) {
        try {
            return method.invoke(configurationClass, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
