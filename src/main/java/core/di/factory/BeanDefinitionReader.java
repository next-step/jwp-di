package core.di.factory;

/**
 * @author hyeyoom
 */
public interface BeanDefinitionReader {

    /**
     * Load definitions from class
     * @param classes class to be {@link BeanDefinition}
     */
    void loadBeanDefinitions(Class<?>... classes);
}
