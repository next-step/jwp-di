package core.di.beans.getter;

@FunctionalInterface
public interface BeanGettable {
    <T> T getBean(Class<?> type);
}
