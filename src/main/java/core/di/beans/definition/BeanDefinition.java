package core.di.beans.definition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface BeanDefinition {
    Class<?> getType();
    Constructor<?> getConstructor();
    Set<Field> getFields();
    Method getMethod();
    InjectType getInjectType();
    boolean containsAnnotation(Class<? extends Annotation> annotationType);
}
