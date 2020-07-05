package core.di.factory;

/**
 * @author hyeyoom
 */
public interface BeanFactory {

    void initialize();

    /**
     * @param requiredType type of bean to find
     * @param <T>          type of bean
     * @return bean
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * @return types of beans
     */
    Class<?>[] getBeanClasses();

    /**
     * @param clazz          origin class
     * @param beanDefinition bean definition data
     */
    void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition);
}
