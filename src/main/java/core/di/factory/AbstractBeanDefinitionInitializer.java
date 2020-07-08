package core.di.factory;

import core.annotation.Qualifier;
import core.mvc.tobe.MethodParameter;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author KingCjy
 */
public abstract class AbstractBeanDefinitionInitializer implements BeanInitializer {

    @Override
    public Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        return support(beanDefinition) ? instantiateBean(beanDefinition, beanFactory) : null;
    }

    abstract public Object instantiateBean(BeanDefinition beanDefinition, BeanFactory beanFactory);
}
