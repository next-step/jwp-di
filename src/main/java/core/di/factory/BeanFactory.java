package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface BeanFactory {

    <T> T getBean(Class<T> clazz);

    Map<Class<?>, Object> getAnnotationTypeClass(Class<? extends Annotation> annotation);
}
