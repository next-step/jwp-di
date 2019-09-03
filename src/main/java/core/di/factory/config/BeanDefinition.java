package core.di.factory.config;

import java.lang.reflect.Constructor;
import java.util.Optional;

public interface BeanDefinition {
    Optional<Constructor<?>> getInjectConstructor();

    Class<?> getBeanClass();

    boolean isAnnotatedDefinition();
}
