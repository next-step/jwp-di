package core.di.factory;

import java.lang.annotation.Annotation;

public interface BeanDefinition {
    Class<?> getBeanType();
    boolean isAnnotation(Class<? extends Annotation> annotation);
}
