package core.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface BeanDefinition {
    String getName();
    Method getMethod();
    Constructor getConstructor();
    Class<?> getBeanClass();
}
