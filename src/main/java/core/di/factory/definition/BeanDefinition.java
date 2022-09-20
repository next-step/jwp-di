package core.di.factory;

import java.lang.reflect.Method;

public interface BeanDefinition {

    Class<?> getClazz();

    Class<?>[] parameterTypes();

    Object instantiate(Object[] objects);
}
