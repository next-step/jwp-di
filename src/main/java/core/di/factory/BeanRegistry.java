package core.di.factory;

import java.util.HashMap;
import java.util.Map;

public class BeanRegistry {

    private final Map<Class<?>, Object> beans = new HashMap<>();

    public boolean contains(Object beanType) {
        if (beanType instanceof Class) {
            return beans.containsKey(beanType);
        }
        return false;
    }

    public <T> T getBean(Class<T> beanType) {
        return (T) beans.get(beanType);
    }

    public void put(Class<?> beanType, Object bean) {
        this.beans.put(beanType, bean);
    }
}
