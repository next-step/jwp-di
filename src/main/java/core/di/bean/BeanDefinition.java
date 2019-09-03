package core.di.bean;

import java.lang.reflect.Constructor;

/**
 * Created by hspark on 2019-09-01.
 */
public interface BeanDefinition {
    Class<?> getBeanClass();
    Constructor<?> getInjectedConstructor() throws NoSuchMethodException;
    Class[] getInjectParameterClasses() throws NoSuchMethodException;
}
