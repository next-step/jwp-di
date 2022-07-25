package core.di.factory;

import com.google.common.collect.Maps;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory implements BeanDefinitionRegistry {
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanDefinitions.put(clazz, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        getBeanClasses().forEach(clazz -> beans.put(clazz, getObject(clazz)));
    }

    public Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> clazz) {
        return beans.entrySet()
                .stream()
                .filter(it -> it.getKey().isAnnotationPresent(clazz))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<Class<?>> getBeanClasses() {
        return beanDefinitions.keySet();
    }

    private Object getObject(Class<?> clazz) {
        BeanDefinition beanDefinition = beanDefinitions.get(clazz);

        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            Method method = ((AnnotatedBeanDefinition) beanDefinition).getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] beanParameters = getBeanParameters(parameterTypes);

            try {
                return method.invoke(((AnnotatedBeanDefinition) beanDefinition).getConfigurationClass().newInstance(), beanParameters);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        Constructor<?> constructor = beanDefinition.getInjectedConstructor();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] beanParameters = getBeanParameters(parameterTypes);

        try {
            return constructor.newInstance(beanParameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] getBeanParameters(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(this::createOrGetBean)
                .toArray();
    }

    private Object createOrGetBean(Class<?> constructorParameterClass) {
        try {
            return getObject(BeanFactoryUtils.findConcreteClass(constructorParameterClass, getBeanClasses()));
        } catch (IllegalStateException e) {
            Object bean = getObject(constructorParameterClass);
            beans.put(beanDefinitions.get(constructorParameterClass).getBeanClass(), bean);
            return bean;
        }
    }
}
