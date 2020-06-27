package core.di.factory;

import java.lang.reflect.Method;
import java.util.Objects;

public class BeanInitInfo {
    private final Class<?> classType;
    private final Method method;
    private final BeanType beanType;

    public BeanInitInfo(Class<?> classType, BeanType beanType) {
        this(classType, null, beanType);
    }

    public BeanInitInfo(Class<?> classType, Method method, BeanType beanType) {
        this.classType = classType;
        this.method = method;
        this.beanType = beanType;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanInitInfo that = (BeanInitInfo) o;
        return Objects.equals(classType, that.classType) &&
                Objects.equals(method, that.method) &&
                beanType == that.beanType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classType, method, beanType);
    }
}
