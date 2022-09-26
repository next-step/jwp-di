package core.di;

import java.lang.reflect.Method;

public interface BeanDefinition {
    Class<?> getBeanClass();
    Method getBeanMethod();
    Class<?> methodReturnType();
    Boolean isMethodReturnTypeEqual(Class<?> methodReturnType);
}
