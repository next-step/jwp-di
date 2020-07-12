package core.di.context;

import java.lang.annotation.Annotation;

public interface ApplicationContext {
    Object[] getBeans(Class<? extends Annotation> controllerClass);

    <T>T getBean(Class<T> clazz);
}
