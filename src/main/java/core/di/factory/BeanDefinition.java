package core.di.factory;

import java.lang.reflect.Method;

public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";

    Class<?> getClazz();

    Method getMethod();

    String getScope();

    boolean isSingleton();
}
