package core.di;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author : yusik
 * @date : 31/08/2019
 */
public class BeanDefinition {

    private Class<?> type;
    private Object factory;
    private Method factoryMethod;

    public BeanDefinition(Class<?> type) {
        this.type = type;
    }

    public BeanDefinition(Object factory, Method factoryMethod) {
        this.type = factoryMethod.getReturnType();
        this.factory = factory;
        this.factoryMethod = factoryMethod;
    }

    public boolean isFactoryBean() {
        return Objects.nonNull(factory) && Objects.nonNull(factoryMethod);
    }

    public Class<?> getType() {
        return type;
    }

    public Object getFactory() {
        return factory;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanDefinition)) return false;

        BeanDefinition that = (BeanDefinition) o;

        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
