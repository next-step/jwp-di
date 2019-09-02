package core.di.factory.config;

import java.lang.reflect.Method;
import java.util.Objects;

public class AnnontatedBeanDefinition extends DefaultBeanDefinition {
    private Method method;

    public AnnontatedBeanDefinition(Class clazz, Method method) {
        super(clazz);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean isAnnotatedDefinition() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnontatedBeanDefinition that = (AnnontatedBeanDefinition) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
