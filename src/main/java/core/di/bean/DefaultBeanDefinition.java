package core.di.bean;

import core.di.factory.BeanFactoryUtils;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Created by hspark on 2019-09-01.
 */
public class DefaultBeanDefinition implements BeanDefinition {
    private Class<?> beanClass;

    public DefaultBeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Constructor<?> getInjectedConstructor() throws NoSuchMethodException {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClass);

        if (Objects.isNull(injectedConstructor)) {
            return beanClass.getConstructor();
        }

        return injectedConstructor;
    }

    @Override
    public Class<?>[] getInjectParameterClasses() throws NoSuchMethodException {
        Constructor<?> constructor = getInjectedConstructor();
        if (constructor.getParameterCount() == 0) {
            return new Class<?>[0];
        }

        return getInjectedConstructor().getParameterTypes();
    }

}
