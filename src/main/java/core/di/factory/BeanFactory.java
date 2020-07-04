package core.di.factory;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanFactory {

    private Set<Class<?>> preInstantiateBeans;
    private Deque<Class<?>> beanInstantiateHistory = new ArrayDeque<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            instantiate(preInstantiateBean);
        }
    }

    private Object instantiate(Class<?> preInstantiateBean) {
        if (beans.containsKey(preInstantiateBean)) {
            return beans.get(preInstantiateBean);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiateBean);
        if (injectedConstructor == null) {
            Object instance = BeanUtils.instantiateClass(preInstantiateBean);
            this.beans.put(preInstantiateBean, instance);
            return instance;
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteParameterType = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstantiateBeans);
            parameterInstances[i] = instantiate(concreteParameterType);
        }

        Object instance = BeanUtils.instantiateClass(injectedConstructor, parameterInstances);
        this.beans.put(preInstantiateBean, instance);
        return instance;
    }
}
