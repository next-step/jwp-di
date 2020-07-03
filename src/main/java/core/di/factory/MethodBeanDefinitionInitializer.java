package core.di.factory;

import core.annotation.Qualifier;
import core.mvc.tobe.MethodParameter;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.annotation.Nullable;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author KingCjy
 */
public class MethodBeanDefinitionInitializer extends AbstractBeanDefinitionInitializer {

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition instanceof MethodBeanDefinition;
    }

    @Nullable
    @Override
    public Object instantiateBean(BeanDefinition definition, BeanFactory beanFactory) {
        MethodBeanDefinition beanDefinition = (MethodBeanDefinition) definition;

        Object instance = beanFactory.getBean(beanDefinition.getParent());
        Method method = beanDefinition.getMethod();

        MethodParameter[] methodParameters = getMethodParameters(method);
        Object[] parameters = getParameters(beanFactory, methodParameters);

        return invokeMethod(instance, method, parameters);
    }

    private Object invokeMethod(Object instance, Method method, Object[] parameters) {
        try {
            return method.invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(method, "Cannot access method '" + method.getName() + "' is it public?", e);
        } catch (InvocationTargetException e) {
            throw new BeanInstantiationException(method, "Method threw exception", e.getTargetException());
        }
    }
}