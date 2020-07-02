package core.di.factory;

import java.lang.annotation.Annotation;

/**
 * @author KingCjy
 */
public interface BeanFactory {
    Object getBean(String name);
    <T> T getBean(Class<T> requireType);
    <T> T getBean(String name, Class<T> requireType);
    Object[] getAnnotatedBeans(Class<? extends Annotation> annotation);
}
