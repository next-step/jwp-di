package core.di;

import java.lang.reflect.Method;

public class BeanDefinition {

    private final Class<?> clazz;
    private final Method method;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, null);
    }

    public BeanDefinition(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public boolean hasMethod() {
        return method != null;
    }

    public Method getMethod() {
        return method;
    }
}
