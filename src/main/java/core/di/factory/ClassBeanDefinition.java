package core.di.factory;

import core.annotation.Lazy;
import org.springframework.beans.BeanUtils;
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
public class ClassBeanDefinition implements BeanDefinition, InstantiatableBean {

    private final Class<?> originClass;
    private final List<Class<?>> dependencies = new ArrayList<>();
    private Constructor<?> beanConstructor;

    public ClassBeanDefinition(Class<?> originClass) {
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

    @Override
    public boolean isLazyInit() {
        final Lazy annotation = originClass.getAnnotation(Lazy.class);
        if (annotation == null) {
            return false;
        }
        return annotation.value();
    }

    @Override
    public Object instantiate(List<Object> dependencies) {
        return BeanUtils.instantiateClass(getBeanConstructor(), dependencies.toArray());
    }
}
