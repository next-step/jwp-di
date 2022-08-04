package core.di.factory;

import core.annotation.Bean;
import core.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigurationBeanFactory extends AbstractBeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanFactory.class);

    public ConfigurationBeanFactory() {
    }

    @Override
    public void register(Set<Class<?>> configurationClasses) {
        registerConfigurationBeans(configurationClasses);
        Map<? extends Class<?>, Method> beanTypes = getBeanTypes(configurationClasses);

        for (Class<?> configurationClass : configurationClasses) {
            Object configurationBeans = beans.get(configurationClass);

            Arrays.stream(configurationClass.getDeclaredMethods())
                  .filter(method -> !beans.containsKey(method.getReturnType()) && method.isAnnotationPresent(Bean.class))
                  .forEach(method -> registerBeans(configurationBeans, beanTypes, method));
        }
    }

    private Map<? extends Class<?>, Method> getBeanTypes(Set<Class<?>> configurationClasses) {
        return configurationClasses.stream()
                                   .map(configurationClass -> Arrays.asList(configurationClass.getDeclaredMethods()))
                                   .flatMap(Collection::stream)
                                   .filter(method -> method.isAnnotationPresent(Bean.class))
                                   .collect(Collectors.toMap(Method::getReturnType, Function.identity()));
    }

    private void registerConfigurationBeans(Set<Class<?>> configurationClasses) {
        configurationClasses.forEach(bean -> Arrays.stream(bean.getDeclaredConstructors())
                                                   .filter(constructor -> !constructor.isAnnotationPresent(Inject.class))
                                                   .forEach(__ -> {
                                                       Object obj = BeanUtils.instantiateClass(bean);
                                                       Arrays.stream(bean.getInterfaces()).forEach(aClass -> beans.put(aClass, obj));
                                                       beans.put(bean, obj);
                                                   }));
    }

    private void registerBeans(Object configurationBeans, Map<? extends Class<?>, Method> beanTypes, Method method) {
        if (method.getParameterCount() > 0) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            Arrays.stream(parameterTypes)
                  .filter(parameterType -> !beans.containsKey(parameterType))
                  .forEach(parameterType -> registerBeans(configurationBeans, beanTypes, beanTypes.get(parameterType)));

            Object[] objects = Arrays.stream(parameterTypes)
                                     .map(parameterType -> beans.get(parameterType))
                                     .collect(Collectors.toList())
                                     .toArray();

            registerBean(configurationBeans, method, objects);
            return;
        }

        registerBean(configurationBeans, method);
    }

    private void registerBean(Object configurationBeans, Method method, Object... objects) {
        try {
            Object bean = method.invoke(configurationBeans, objects);
            logger.debug("register configuration bean {}", bean.getClass());
            Arrays.stream(method.getReturnType().getInterfaces()).forEach(aClass -> beans.put(aClass, bean));
            beans.put(method.getReturnType(), bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
