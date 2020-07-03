package core.di.factory;

import java.lang.reflect.Method;

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
    public String toString() {
        return "MethodBeanDefinition{" +
                "parent=" + parent +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", method=" + method +
                '}';
    }
}
