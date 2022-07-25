package core.di.factory;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class ClasspathBeanDefinition implements BeanDefinition {

    private final Class<?> beanClass;

    public ClasspathBeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Constructor<?> getInjectedConstructor() {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClass);

        if (Objects.isNull(injectedConstructor)) {
            try {
                return beanClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return injectedConstructor;
    }
}
