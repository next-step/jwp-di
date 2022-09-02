package core.di.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BeanRegister extends Observer{

    default void initialize() {}

    Class<?> type();

    List<Class<?>> interfaces();

    Object newInstance(Object[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException;

    Class<?>[] getParameterTypes();

    int getParameterCount();

    int priority();
}
