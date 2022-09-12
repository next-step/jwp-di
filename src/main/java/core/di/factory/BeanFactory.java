package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return this.beans.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstantiateBeans.forEach(preInstantiateBean -> this.beans.put(preInstantiateBean, instantiate(preInstantiateBean)));
    }

    private Object instantiate(Class<?> preInstantiateBean) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiateBean);
        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(preInstantiateBean);
        }
        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateConstructor(Constructor<?> injectedConstructor) {
        return BeanUtils.instantiateClass(injectedConstructor, getInstantiatedParameters(injectedConstructor));
    }

    private Object[] getInstantiatedParameters(Constructor<?> injectedConstructor) {
        return Arrays.stream(injectedConstructor.getParameterTypes())
                .map(parameterType -> {
                    Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, this.preInstantiateBeans);
                    return instantiate(concreteClass);
                }).toArray();
    }
}
