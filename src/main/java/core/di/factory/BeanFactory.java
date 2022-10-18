package core.di.factory;

import core.di.factory.bean.Bean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanFactory {
    private final Map<Class<?>, Bean> beanByType;
    private final Map<Class<?>, Object> instanceByType = new HashMap<>();

    public BeanFactory(Collection<Bean> instanceByType) {
        this.beanByType = instanceByType.stream()
                .collect(Collectors.toUnmodifiableMap(Bean::getType, Function.identity()));
    }

    public void initialize() {
        beanByType.values().forEach(this::createInstance);
    }
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) instanceByType.get(requiredType);
    }

    private Object createInstance(Bean bean) {
        if (instanceByType.containsKey(bean.getType())) {
            return instanceByType.get(bean.getType());
        }

        Object instance = instantiateClass(bean);
        instanceByType.put(bean.getType(), instance);
        return instance;
    }

    private Object instantiateClass(Bean bean) {
        if (bean.isNotInstanced()) {
            return createInstance(beanByType.get(getSubType(bean.getType())));
        }

        return bean.instantiate(getParameters(bean));
    }

    private Class<?> getSubType(Class<?> clazz) {
        return beanByType.keySet()
                .stream()
                .filter(type -> !type.equals(clazz) && clazz.isAssignableFrom(type))
                .findAny()
                .orElseThrow(() -> new NoSuchBeanDefinitionException(clazz));
    }
    private List<Object> getParameters(Bean bean) {
        return bean.getParameterTypes()
                .stream()
                .map(beanByType::get)
                .map(this::createInstance)
                .collect(Collectors.toList());
    }

    public Collection<Object> annotatedWith(Class<? extends Annotation> annotation) {
        return instanceByType.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}
