package core.di.factory;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Interface to be implemented by objects that define information for instantiation of bean.
 * This interface is simplified version of Spring BeanDefinition interface.
 *
 * @author hyeyoom
 */
public interface BeanDefinition {

    /**
     * Origin class of bean. This field is used to instantiate bean.
     * @return class
     */
    Class<?> getOriginalClass();

    /**
     * @return dependencies of this bean!
     */
    @Nullable
    List<Class<?>> getDependencies();

    /**
     * adds dependencies
     * @param clazz dependencies of this bean.
     */
    void setDependencies(Class<?> ...clazz);
}
