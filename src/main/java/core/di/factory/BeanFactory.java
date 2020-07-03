package core.di.factory;

import jdk.internal.jline.internal.Nullable;

import java.lang.annotation.Annotation;

/**
 * @author KingCjy
 */
public interface BeanFactory {
    @Nullable
    Object getBean(String name);

    @Nullable
    <T> T getBean(Class<T> requireType);

    @Nullable
    <T> T getBean(String name, Class<T> requireType);

    @Nullable
    Object[] getAnnotatedBeans(Class<? extends Annotation> annotation);
}
