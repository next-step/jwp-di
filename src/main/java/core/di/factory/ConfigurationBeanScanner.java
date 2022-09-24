package core.di.factory;

import core.annotation.Bean;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {
    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        final Object instance = BeanUtils.instantiateClass(clazz);
        final Map<? extends Class<?>, Object> beans = Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(Method::getReturnType, method -> instanticateBean(method, instance)));
        beanFactory.addBean(beans);
    }

    private Object instanticateBean(Method method, Object instance) {
        return ReflectionUtils.invokeMethod(method, instance);
    }
}
