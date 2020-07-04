package core.di.beans.injector;

import core.di.beans.definition.BeanDefinition;
import core.di.beans.getter.BeanGettable;
import org.springframework.beans.BeanUtils;

public class DefaultBeanInjector implements BeanInjector {
    @Override
    public <T> T inject(BeanGettable beanGettable, BeanDefinition beanDefinition) {
        return (T) BeanUtils.instantiateClass(beanDefinition.getType());
    }
}
