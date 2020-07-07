package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.web.Controller;
import core.exception.JwpException;
import core.exception.JwpExceptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> instanceMap = new HashMap<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> scanBeans) {
        Map<Class<?>, Object> preInstanticateBeanMap = new HashMap<>();
        for (Class<?> scanBean : scanBeans) {
            if (!scanBean.isAnnotationPresent(Configuration.class)) {
                Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(scanBean);
                preInstanticateBeanMap.put(scanBean, constructor);
                continue;
            }
            addConfigurationBeans(scanBean, preInstanticateBeanMap);
        }
        initialize(preInstanticateBeanMap);
    }

    private void addConfigurationBeans(Class<?> scanBean, Map<Class<?>, Object> preInstanticateBeanMap) {
        Object instance = newInstance(scanBean);
        for (Method method : scanBean.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                instanceMap.put(method.getReturnType(), instance);
                preInstanticateBeanMap.put(method.getReturnType(), method);
            }
        }
    }

    private void initialize(Map<Class<?>, Object> preInstanticateBeanMap) {
        for (Map.Entry<Class<?>, Object> preInstanticateBeanEntry : preInstanticateBeanMap.entrySet()) {
            Class<?> clazz = preInstanticateBeanEntry.getKey();
            Object target = preInstanticateBeanEntry.getValue();

            if (beans.containsKey(clazz)) {
                continue;
            }
            beans.put(clazz, getInstance(clazz, target, preInstanticateBeanMap));
        }
    }

    private Object getInstance(Class<?> clazz, Object target, Map<Class<?>, Object> preInstanticateBeanMap) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        if (Objects.isNull(target)) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, getPreInstanticateBeanClass(preInstanticateBeanMap));
            return BeanUtils.instantiateClass(concreteClass);
        }

        if (target instanceof Constructor) {
            beans.put(clazz, getConstructorNewInstance((Constructor<?>) target, preInstanticateBeanMap));
            return beans.get(clazz);
        }

        if (target instanceof Method) {
            Method method = (Method) target;
            List<Object> parameters = new ArrayList<>();
            for (Parameter parameter : method.getParameters()) {
                parameters.add(getInstance(parameter.getType(), preInstanticateBeanMap.get(parameter.getType()), preInstanticateBeanMap));
            }
            beans.put(clazz, methodInvoke(method, clazz, preInstanticateBeanMap));
        }
        return beans.get(clazz);
    }

    private Object getConstructorNewInstance(Constructor<?> constructor, Map<Class<?>, Object> preInstanticateBeanMap) {
        try {
            if (constructor.getParameterCount() > 0) {
                return constructor.newInstance(getInitArgs(constructor, preInstanticateBeanMap));
            }
            return constructor.newInstance();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new JwpException(JwpExceptionStatus.CONSTRUCTOR_NEW_INSTANCE_FAIL, e);
        }
    }

    private Object[] getMethodInitArgs(Method method, Map<Class<?>, Object> preInstanticateBeanMap) {
        List<Object> parameters = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(getInstance(parameter.getType(), preInstanticateBeanMap.get(parameter.getType()), preInstanticateBeanMap));
        }

        return parameters.toArray(new Object[parameters.size()]);
    }

    private Object[] getInitArgs(Constructor<?> constructor, Map<Class<?>, Object> preInstanticateBeanMap) {
        Object[] initArgs = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            Class<?> type = constructor.getParameterTypes()[i];
            initArgs[i] = getInstance(type, preInstanticateBeanMap.get(type), preInstanticateBeanMap);
        }
        return initArgs;
    }

    private Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new JwpException(JwpExceptionStatus.NEW_INSTANCE_FAIL, e);
        }
    }

    private Object methodInvoke(Method method, Class<?> clazz, Map<Class<?>, Object> preInstanticateBeanMap) {
        try {
            return method.invoke(instanceMap.get(clazz), getMethodInitArgs(method, preInstanticateBeanMap));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JwpException(JwpExceptionStatus.METHOD_INVOKE_FAIL, e);
        }
    }

    private Set<Class<?>> getPreInstanticateBeanClass(Map<Class<?>, Object> preInstanticateBeanMap) {
        return preInstanticateBeanMap.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        return getBeansAnnotationWith(Controller.class);
    }

    private Map<Class<?>, Object> getBeansAnnotationWith(Class<? extends Annotation> annotationClass) {
        return this.beans.entrySet().stream()
                .filter(beanEntry -> beanEntry.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
