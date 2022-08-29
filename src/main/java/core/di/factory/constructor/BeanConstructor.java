package core.di.factory.constructor;

import java.util.List;

public interface BeanConstructor {

    Class<?> type();

    List<Class<?>> parameterTypes();

    default boolean isNotInstanced() {
        return false;
    }

    Object instantiate(List<Object> args);
}
