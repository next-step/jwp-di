package core.di.factory;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Optional;

public class NoneParameterBeanMethodInitializer implements BeanMethodInitializer {

    @Override
    public boolean support(Method method) {
        return method.getParameterCount() == 0;
    }

    @Override
    public Optional<Object> initialize(BeanRegistry beanRegistry, Method method) {
        Object bean = instantiateBean(method);
        beanRegistry.put(method.getReturnType(), bean);
        return Optional.of(bean);
    }

    private Object instantiateBean(Method method) {
        Object instance = BeanUtils.instantiateClass(method.getDeclaringClass());
        return ReflectionUtils.invokeMethod(method, instance);
    }

}
