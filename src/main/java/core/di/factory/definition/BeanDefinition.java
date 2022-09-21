package core.di.factory.definition;

import java.lang.reflect.Method;

public interface BeanDefinition {

    Class<?> getClazz();

    Class<?>[] parameterTypes();

    Object instantiate(Object[] objects);
}
