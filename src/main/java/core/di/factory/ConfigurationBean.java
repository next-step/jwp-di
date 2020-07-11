package core.di.factory;

import core.annotation.Bean;
import core.exception.JwpException;
import core.exception.JwpExceptionStatus;

import java.lang.reflect.Method;
import java.util.Map;

public class ConfigurationBean implements BeanAdapter {
    private Class<?> clazz;

    public ConfigurationBean(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void addBean(Map<Class<?>, Object> preInstanticateBeanMap, Map<Class<?>, Object> instanceMap) {
        Object instance = newInstance(this.clazz);
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                instanceMap.put(method.getReturnType(), instance);
                preInstanticateBeanMap.put(method.getReturnType(), method);
            }
        }
    }

    private Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new JwpException(JwpExceptionStatus.NEW_INSTANCE_FAIL, e);
        }
    }
}
