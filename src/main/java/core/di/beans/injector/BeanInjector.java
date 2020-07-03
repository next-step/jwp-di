package core.di.beans.injector;

import core.di.beans.definition.BeanDefinition;
import core.di.beans.getter.BeanGettable;

@FunctionalInterface
public interface BeanInjector {
    <T> T inject(BeanGettable beanGettable, BeanDefinition beanDefinition);
}
