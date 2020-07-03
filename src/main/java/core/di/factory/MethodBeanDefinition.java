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

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
