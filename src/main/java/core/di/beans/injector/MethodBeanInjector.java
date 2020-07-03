package core.di.beans.injector;

import core.di.beans.definition.BeanDefinition;
import core.di.beans.getter.BeanGettable;
import core.di.factory.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MethodBeanInjector implements BeanInjector {
    @Override
    public <T> T inject(BeanGettable beanGettable, BeanDefinition beanDefinition) {
        Method method = beanDefinition.getMethod();
        return (T) BeanFactoryUtils.invokeMethod(
            method,
            beanGettable.getBean(method.getDeclaringClass()),
            BeanFactoryUtils.getArguments(beanGettable, method.getParameterTypes())
        );
    }
}
