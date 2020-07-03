package core.di.factory;

import core.mvc.tobe.MethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author KingCjy
 */
public class MethodBeanDefinitionInitializer extends AbstractBeanDefinitionInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MethodBeanDefinition.class);

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition instanceof MethodBeanDefinition;
    }

    @Override
    public Object instantiateBean(BeanDefinition definition, BeanFactory beanFactory) {
        MethodBeanDefinition beanDefinition = (MethodBeanDefinition) definition;

        Object classInstance = beanFactory.getBean(beanDefinition.getParent());
        Method method = beanDefinition.getMethod();

        MethodParameter[] methodParameters = getMethodParameters(method);
        Object[] parameters = getParameters(beanFactory, methodParameters);

        Object instance = invokeMethod(classInstance, method, parameters);
        logger.info("bean " + instance.getClass() + " instantiate");

        return instance;
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