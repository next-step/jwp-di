package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface Context {
    <T> T getBean(Class<T> clazz);

    Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass);
}
