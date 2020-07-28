package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface BeanDefinition {

    Class<?> getClazz();

    Constructor<?> getInjectedConstructor();

    Method getBeanCreationMethod();

}
