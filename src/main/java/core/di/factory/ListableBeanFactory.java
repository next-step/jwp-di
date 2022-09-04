package core.di.factory;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {

    <T> Map<Class<T>, T> getBeansOfType(Class<T> type);
}
