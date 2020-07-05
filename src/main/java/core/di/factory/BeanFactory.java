package core.di.factory;

import com.google.common.collect.Maps;
import core.di.factory.exception.CircularReferenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class BeanFactory {

    private Set<Class<?>> preInstantiateBeans;
    private Deque<Class<?>> beanInstantiateHistory = new ArrayDeque<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            instantiate(preInstantiateBean);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Set<Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return this.beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    // TODO: 2020/07/05 refactor
    private Object instantiate(Class<?> preInstantiateBean) {
        if (this.beanInstantiateHistory.contains(preInstantiateBean)) {
            throw new CircularReferenceException("Illegal Bean Creation Exception : Circular Reference");
        }

        if (beans.containsKey(preInstantiateBean)) {
            return beans.get(preInstantiateBean);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiateBean);
        if (injectedConstructor == null) {
            Object instance = BeanUtils.instantiateClass(preInstantiateBean);
            this.beans.put(preInstantiateBean, instance);
            return instance;
        }

        this.beanInstantiateHistory.push(preInstantiateBean);

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteParameterType = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstantiateBeans);
            parameterInstances[i] = instantiate(concreteParameterType);
        }

        Object instance = BeanUtils.instantiateClass(injectedConstructor, parameterInstances);
        this.beans.put(preInstantiateBean, instance);

        this.beanInstantiateHistory.pop();
        return instance;
    }
}
