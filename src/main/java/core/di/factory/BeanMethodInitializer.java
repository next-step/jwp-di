package core.di.factory;

import java.lang.reflect.Method;

public interface BeanMethodInitializer {

    boolean support(Method method);

    Object initialize(BeanRegistry beanRegistry, Method method);

}
