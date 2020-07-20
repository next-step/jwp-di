package core.di.factory;

import core.annotation.Bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        try {
            Object instance = clazz.newInstance();
            Map<Class<?>, Object> beans = new HashMap<>();
            List<Method> beanMethods = Arrays.stream(clazz.getMethods())
                    .filter(m -> m.isAnnotationPresent(Bean.class))
                    .collect(Collectors.toList());

            for (Method beanMethod : beanMethods) {
                Object result = beanMethod.invoke(instance);
                beans.put(beanMethod.getReturnType(), result);
            }

            beanFactory.addBeans(beans);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
