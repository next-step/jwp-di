package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.di.Autowire;
import core.di.BeanDefinition;
import core.di.BeanDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private static final Logger log = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

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
            Object[] arguments = getArguments(method.getParameterTypes());
            bean = createBeanByMethod(method, arguments);
            beans.put(requiredType, bean);
            return (T) bean;
        }

        bean = createAutowireBean(requiredType);
        beans.put(requiredType, bean);
        return (T) bean;
    }

    private Object createBeanByMethod(Method method, Object[] arguments) {
        try {
            return method.invoke(getBean(method.getDeclaringClass()), arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Object createAutowireBean(Class<T> beanClass) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, getBeanClasses());
        log.info("concrete class = {}", concreteClass);
        BeanDefinition beanDefinition = beanDefinitions.get(concreteClass);
        Autowire autowire = beanDefinition.getResolvedAutowireMode();

        if (autowire == Autowire.CONSTRUCTOR) {
            Constructor<?> constructor = beanDefinition.getConstructor();
            Object[] arguments = getArguments(constructor.getParameterTypes());
            return BeanUtils.instantiateClass(constructor, arguments);
        }

        return BeanUtils.instantiateClass(concreteClass);
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
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> beansOfType = Maps.newHashMap();
        this.beans.entrySet()
            .stream()
            .filter(entry -> type.isAssignableFrom(entry.getKey()))
            .forEach(entry -> beansOfType.put(beanDefinitions.get(entry.getKey()).getBeanName(), (T) entry.getValue()));
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
