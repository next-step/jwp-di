package core.di.factory;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class MultiParameterBeanMethodInitializer implements BeanMethodInitializer {

    private final BeanFactory beanFactory;

    public MultiParameterBeanMethodInitializer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean support(Method method) {
        return method.getParameterCount() > 0;
    }

    @Override
    public Object initialize(BeanRegistry beanRegistry, Method method) {
        Object instance = BeanUtils.instantiateClass(method.getDeclaringClass());
        Object[] parameters = getBeanMethodParameterInstances(beanRegistry, method);
        Object bean = ReflectionUtils.invokeMethod(method, instance, parameters);
        beanRegistry.put(method.getReturnType(), bean);
        return bean;
    }

    private Object[] getBeanMethodParameterInstances(BeanRegistry beanRegistry, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] results = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            results[i] = getMethodParameterInstance(beanRegistry, parameterTypes[i]);
        }
        return results;
    }

    private Object getMethodParameterInstance(BeanRegistry beanRegistry, Class<?> parameterType) {
        if (beanRegistry.contains(parameterType)) {
            return beanRegistry.getBean(parameterType);
        }
        return beanFactory.getBean(parameterType);
    }
}
