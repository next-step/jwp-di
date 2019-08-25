package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> annotation) {
        return beans.entrySet().stream().filter(it -> it.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void initialize() {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            Object bean = instantiate(preInstanticateBean);
            beans.put(preInstanticateBean, bean);
        }
    }

    private Object instantiate(Class<?> beanClass) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClass);

        if (Objects.isNull(injectedConstructor)) {
            return BeanUtils.instantiateClass(beanClass);
        }

        Class<?>[] constructorParameterClasses = injectedConstructor.getParameterTypes();
        Object[] args = getConstructorParameters(constructorParameterClasses);

        return BeanUtils.instantiateClass(injectedConstructor, args);
    }

    private Object[] getConstructorParameters(Class<?>[] constructorParameterClasses) {
        return Arrays.stream(constructorParameterClasses)
                .map(this::createOrGetBean)
                .collect(Collectors.toList())
                .toArray();
    }

    private Object createOrGetBean(Class<?> constructorParameterClass) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(constructorParameterClass, preInstanticateBeans);
        Object bean = beans.get(concreteClass);
        if (Objects.isNull(bean)) {
            bean = instantiate(concreteClass);
            beans.put(concreteClass, bean);
        }
        return bean;
    }
}
