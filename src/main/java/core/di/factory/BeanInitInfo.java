package core.di.factory;

import java.lang.reflect.Method;

public class BeanInitInfo {
    private Class<?> clazz;
    private Method method;
    private BeanType beanType;

    public BeanType getBeanType() {
        return beanType;
    }
}
