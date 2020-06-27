package core.di.factory;

import java.util.Objects;

public class BeanInitInfo {
    private final Class<?> classType;
    private final MethodInfo MethodInfo;
    private final BeanType beanType;

    public BeanInitInfo(Class<?> classType, BeanType beanType) {
        this(classType, null, beanType);
    }

    public BeanInitInfo(Class<?> classType, MethodInfo MethodInfo, BeanType beanType) {
        this.classType = classType;
        this.MethodInfo = MethodInfo;
        this.beanType = beanType;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public MethodInfo getMethodInfo() {
        return MethodInfo;
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
                Objects.equals(MethodInfo, that.MethodInfo) &&
                beanType == that.beanType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classType, MethodInfo, beanType);
    }
}
