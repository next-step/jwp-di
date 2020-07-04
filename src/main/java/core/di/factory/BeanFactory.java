package core.di.factory;

/**
 * @author hyeyoom
 */
public interface BeanFactory {

    void instantiate();

    /**
     * @param requiredType type of bean to find
     * @param <T>          type of bean
     * @return bean
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * @param clazz          origin class
     * @param beanDefinition bean definition data
     */
    void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition);
}
