package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionSupport {

    public static Object[] getArguments(Constructor<?> constructor, ArgumentMapper am) {
        Object[] args = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Class<?> parameterType = constructor.getParameterTypes()[i];
            args[i] = am.getArgument(parameterType);
        }
        return args;
    }

    public static void setFieldByForce(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(field.getName() + " access failed");
        }
    }

}
