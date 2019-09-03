package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.List;

public interface BeanDefinition {
    Class<?> getBeanType();

    Class<?>[] getParameters();

    boolean isAnnotation(Class<? extends Annotation> annotation);

    Object newInstance(List<Object> parameters);
}
