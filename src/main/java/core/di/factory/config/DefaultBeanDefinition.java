package core.di.factory.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;

public class DefaultBeanDefinition implements BeanDefinition{
    public DefaultBeanDefinition(Class<?> clazz) {

    }

    @Override
    public Constructor<?> getInjectConstructor() {
        return null;
    }

    @Override
    public Set<Field> getInjectFields() {
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return null;
    }
}
