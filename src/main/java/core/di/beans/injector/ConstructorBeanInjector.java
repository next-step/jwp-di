package core.di.beans.injector;

import core.di.beans.definition.BeanDefinition;
import core.di.beans.getter.BeanGettable;
import core.di.factory.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;

public class ConstructorBeanInjector implements BeanInjector {
    @Override
    public <T> T inject(BeanGettable beanGettable, BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object[] args = BeanFactoryUtils.getArguments(beanGettable, constructor.getParameterTypes());
        return (T) BeanUtils.instantiateClass(constructor, args);
    }
}
