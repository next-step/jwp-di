package core.di.factory;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

abstract class AbstractBeanFactory implements BeanFactory {
    protected static Map<Class<?>, Object> beans = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    abstract void register(Set<Class<?>> preInstanticateBeans);

}
