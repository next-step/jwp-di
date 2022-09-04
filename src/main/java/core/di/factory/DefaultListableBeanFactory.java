package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.di.Autowire;
import core.di.BeanDefinition;
import core.di.BeanDefinitionRegistry;

public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    @Override
    public void preInstantiateSingletons() {
        for (Class<?> clazz : getBeanClasses()) {
            getBean(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        Object bean = beans.get(requiredType);
        if (bean != null) {
            return (T) bean;
        }

        BeanDefinition beanDefinition = beanDefinitions.get(requiredType);

        if (beanDefinition != null && beanDefinition.hasBeanMethod()) {
            Method method = beanDefinition.getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] arguments = getArguments(parameterTypes);
            try {
                Object beanContainingMethod = getBean(method.getDeclaringClass());
                bean = method.invoke(beanContainingMethod, arguments);
                beans.put(requiredType, bean);
                return (T) bean;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(requiredType, beanDefinitions.keySet());
        beanDefinition = beanDefinitions.get(concreteClass);
        Autowire autowire = beanDefinition.getResolvedAutowireMode();

        if (autowire == Autowire.NO) {
            bean = BeanUtils.instantiateClass(concreteClass);
        }

        if (autowire == Autowire.CONSTRUCTOR) {
            Constructor<?> constructor = beanDefinition.getConstructor();
            Object[] arguments = getArguments(constructor.getParameterTypes());
            bean = BeanUtils.instantiateClass(constructor, arguments);
        }

        beans.put(requiredType, bean);
        return (T) bean;
    }

    private Object[] getArguments(Class<?>[] parameterTypes) {
        List<Object> arguments = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Object bean = getBean(parameterType);
            if (bean == null) {
                throw new IllegalStateException("Cannot autowire bean : " + parameterType);
            }
            arguments.add(bean);
        }
        return arguments.toArray();
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanDefinitions.put(clazz, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<Class<T>, T> getBeansOfType(Class<T> type) {
        Map<Class<T>, T> beansOfType = Maps.newHashMap();
        this.beans.entrySet()
            .stream()
            .filter(entry -> entry.getKey() == type)
            .forEach(entry -> beansOfType.put(type, (T) entry.getValue()));
        return beansOfType;
    }

    @Override
    public Set<Class<?>> getBeanClasses() {
        return beanDefinitions.keySet();
    }

    private Object instantiate(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        Object instance = createBeanInstance(clazz, constructor);
        beans.put(clazz, instance);
        return instance;
    }

    private Object createBeanInstance(Class<?> clazz, Constructor<?> constructor) {
        if (constructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }
        return instantiateConstructor(constructor);
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> arguments = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, getBeanClasses());
            arguments.add(instantiate(concreteClass));
        }
        return BeanUtils.instantiateClass(constructor, arguments.toArray());
    }
}
