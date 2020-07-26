package core.di.factory;

import com.google.common.collect.Maps;
import core.di.factory.exception.CircularReferenceException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor
public class DefaultBeanFactory implements BeanFactory {

    private Deque<Class<?>> beanInstantiateHistory = new ArrayDeque<>();
    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    @Override
    public void initialize() {
        for (Class<?> clazz : beanDefinitions.keySet()) {
            registerBean(clazz);
        }
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        if (beanDefinitions.containsKey(clazz)) {
            throw new IllegalStateException("Bean Definition is duplicate.");
        }

        this.beanDefinitions.put(clazz, beanDefinition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    @Override
    public List<Class<?>> getBeanClasses() {
        return new ArrayList<>(beans.keySet());
    }

    @Override
    public List<Object> getBeans() {
        return new ArrayList<>(beans.values());
    }

    private Object registerBean(Class<?> preInstantiateBean) {
        if (this.beanInstantiateHistory.contains(preInstantiateBean)) {
            throw new CircularReferenceException("Illegal Bean Creation Exception : Circular Reference");
        }

        if (beans.containsKey(preInstantiateBean)) {
            return beans.get(preInstantiateBean);
        }

        this.beanInstantiateHistory.push(preInstantiateBean);
        Object instance = registerBeanWithInstantiating(preInstantiateBean);
        this.beanInstantiateHistory.pop();

        return instance;
    }

    private Object registerBeanWithInstantiating(Class<?> preInstantiateBean) {
        Class<?> concreteBeanClass = BeanInstantiationUtils.findConcreteClass(preInstantiateBean, beanDefinitions.keySet());
        Object instance = instantiate(concreteBeanClass);

        this.beans.put(concreteBeanClass, instance);
        return instance;
    }

    private Object instantiate(Class<?> concreteBeanClass) {
        BeanDefinition beanDefinition = this.beanDefinitions.get(concreteBeanClass);
        Constructor<?> injectedConstructor = beanDefinition.getInjectedConstructor();
        Method beanCreationMethod = beanDefinition.getBeanCreationMethod();

        if (injectedConstructor == null && beanCreationMethod == null) {
            return BeanUtils.instantiateClass(concreteBeanClass);
        }

        if (injectedConstructor != null) {
            Object[] parameterInstances = getParameterInstances(injectedConstructor);
            return BeanUtils.instantiateClass(injectedConstructor, parameterInstances);
        }

        Object[] parameterInstances = getParameterInstances(beanCreationMethod);
        return BeanInstantiationUtils.invokeMethod(beanCreationMethod, parameterInstances);
    }

    private Object[] getParameterInstances(Executable executable) {
        Class<?>[] parameterTypes = executable.getParameterTypes();

        Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteParameterType = findConcreteClass(parameterTypes[i]);
            parameterInstances[i] = registerBean(concreteParameterType);
        }

        return parameterInstances;
    }

    private Class<?> findConcreteClass(Class<?> preInstantiateBean) {
        return BeanInstantiationUtils.findConcreteClass(preInstantiateBean, beanDefinitions.keySet());
    }

}
