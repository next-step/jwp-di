package core.di.factory.bean;

import java.util.List;

public interface Bean {
    Class<?> getType();
    List<Class<?>> getParameterTypes();
    default boolean isNotInstanced() {
        return false;
    }
    Object instantiate(List<Object> args);
}
