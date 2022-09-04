package core.di;

import core.di.factory.ListableBeanFactory;

public interface ApplicationContext extends ListableBeanFactory {

    BeanDefinition getBeanDefinition(Class<?> requiredType);
}
