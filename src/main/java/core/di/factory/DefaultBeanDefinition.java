package core.di.factory;

import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of BeanDefinition
 *
 * @author hyeyoom
 */
public class DefaultBeanDefinition implements BeanDefinition {

    private final Class<?> originClass;
    private final List<Class<?>> dependencies = new ArrayList<>();
    private Constructor<?> beanConstructor;

    public DefaultBeanDefinition(Class<?> originClass) {
        this.originClass = originClass;
    }

    @Override
    public Class<?> getOriginalClass() {
        return originClass;
    }

    @Override
    @Nullable
    public Constructor<?> getBeanConstructor() {
        return beanConstructor;
    }

    @Override
    public void setBeanConstructor(Constructor<?> constructor) {
        this.beanConstructor = constructor;
    }

    @Override
    @Nullable
    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public void setDependencies(Class<?>... clazz) {
        dependencies.addAll(Arrays.asList(clazz));
    }
}
