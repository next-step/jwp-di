package core.di.factory;

import com.google.common.collect.Maps;
import core.di.BeanScanners;
import core.di.factory.exception.CircularReferenceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
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
    private BeanScanners beanScanners;

    public BeanFactory(BeanScanners beanScanners) {
        this.beanScanners = beanScanners;
    }

    public void initialize() {
        this.preInstantiateBeans = beanScanners.scan();

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

    private Object instantiate(Class<?> preInstantiateBean) {
        if (this.beanInstantiateHistory.contains(preInstantiateBean)) {
            throw new CircularReferenceException("Illegal Bean Creation Exception : Circular Reference");
        }

        if (beans.containsKey(preInstantiateBean)) {
            return beans.get(preInstantiateBean);
        }

        this.beanInstantiateHistory.push(preInstantiateBean);
        Object instance = instantiateWithInjectedConstructor(preInstantiateBean);
        this.beanInstantiateHistory.pop();

        return instance;
    }

    private Object instantiateWithInjectedConstructor(Class<?> preInstantiateBean) {
        Class<?>[] parameterTypes = beanScanners.getParameterTypesForInstantiation(preInstantiateBean);
        if (parameterTypes.length == 0) {
            return registerBeanWithInstantiating(preInstantiateBean);
        }

        Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteParameterType = beanScanners.findConcreteClass(parameterTypes[i]);

            parameterInstances[i] = instantiate(concreteParameterType);
        }

        return registerBeanWithInstantiating(preInstantiateBean, parameterInstances);
    }

    private Object registerBeanWithInstantiating(Class<?> preInstantiateBean, Object... parameterInstances) {
        Object instance = beanScanners.instantiate(preInstantiateBean, parameterInstances);

        this.beans.put(preInstantiateBean, instance);
        return instance;
    }

}
