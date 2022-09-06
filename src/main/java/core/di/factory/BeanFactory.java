package core.di.factory;

import java.util.Set;

public interface BeanFactory {

    <T> T getBean(Class<T> requiredType);

    Set<Class<?>> getBeanClasses();
}
