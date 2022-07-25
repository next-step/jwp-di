package core.di.factory;

import java.lang.reflect.Constructor;

public interface BeanDefinition {
    Class<?> getBeanClass();

    Constructor<?> getInjectedConstructor();
}
