package core.di.factory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author KingCjy
 */
public class MethodBeanDefinition implements BeanDefinition {

    private Class<?> parent;
    private Class<?> type;
    private String name;
    private Method method;

    public MethodBeanDefinition(Method method) {
        this.method = method;
        this.parent = method.getDeclaringClass();
        this.type = method.getReturnType();
        this.name = method.getName();
    }

    public Class<?> getParent() {
        return parent;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodBeanDefinition that = (MethodBeanDefinition) o;
        return Objects.equals(parent, that.parent) &&
                Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, type, name, method);
    }

    @Override
    public String toString() {
        return "MethodBeanDefinition{" +
                "parent=" + parent +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", method=" + method +
                '}';
    }
}
