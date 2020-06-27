package core.di.factory;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodInfo {
    private final Class<?> originClass;
    private final Method method;

    public MethodInfo(Class<?> originClass, Method method) {
        this.originClass = originClass;
        this.method = method;
    }

    public Class<?> getOriginClass() {
        return originClass;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(originClass, that.originClass) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originClass, method);
    }
}
