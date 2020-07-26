package core.di.factory;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Getter
@EqualsAndHashCode(of = "clazz")
public class BeanDefinition {

    private Class<?> clazz;
    private Constructor<?> injectedConstructor;
    private Method beanCreationMethod;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, null);
    }

    public BeanDefinition(Class<?> clazz, Method beanCreationMethod) {
        this.clazz = clazz;
        this.injectedConstructor = BeanInstantiationUtils.getInjectedConstructor(clazz);
        this.beanCreationMethod = beanCreationMethod;
    }

    public boolean doesNotExistSpecificWayToInstantiate() {
        return injectedConstructor == null && beanCreationMethod == null;
    }

}
