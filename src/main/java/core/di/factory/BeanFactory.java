package core.di.factory;

public interface BeanFactory {
    <T> T getBean(Class<T> requiredType);
}
