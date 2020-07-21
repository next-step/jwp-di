package core.di.factory;

import java.lang.reflect.Method;

public class ConfigurationBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final Method method;
    private final String scope;

    public ConfigurationBeanDefinition(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
        this.scope = SCOPE_SINGLETON;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Method getMethod() {
        return method;
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
