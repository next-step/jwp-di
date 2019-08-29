package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface BeanFactory {

    Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation);

    <T> T getBean(Class<T> requiredType);

}
