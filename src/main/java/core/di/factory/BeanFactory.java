package core.di.factory;

import java.util.Set;

public interface BeanFactory {
    <T> T getBean(Class<T> requiredType);

    void register(Set<Class<?>> preInstanticateBeans);
}
