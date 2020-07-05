package core.di.context;

public interface ApplicationContext {

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
}
