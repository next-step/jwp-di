package core.di.factory;

import java.lang.reflect.Method;

public class ClasspathBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final String scope;

    public ClasspathBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
        this.scope = SCOPE_SINGLETON;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }
}
