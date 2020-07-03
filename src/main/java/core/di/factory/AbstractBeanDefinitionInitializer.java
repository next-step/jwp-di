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

    protected static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Override
    public Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        if(!support(beanDefinition)) {
            return null;
        }

        return instantiateBean(beanDefinition, beanFactory);
    }

    abstract public Object instantiateBean(BeanDefinition beanDefinition, BeanFactory beanFactory);

    protected Object[] getParameters(BeanFactory beanFactory, MethodParameter[] methodParameters) {
        Object[] parameters = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            String beanName = getBeanName(methodParameters[i]);
            parameters[i] = beanFactory.getBean(beanName, methodParameters[i].getType());
        }

        return parameters;
    }

    protected String getBeanName(MethodParameter methodParameter) {
        String name = methodParameter.getType().getName();
        Qualifier qualifier = methodParameter.getAnnotation(Qualifier.class);

        return qualifier == null ? name : qualifier.value();
    }

    protected MethodParameter[] getMethodParameters(Executable executable) {
        MethodParameter[] methodParameters = new MethodParameter[executable.getParameters().length];
        String[] parameterNames = getParameterNames(executable);
        Parameter[] parameters = executable.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = new MethodParameter(executable, parameters[i].getType(), parameters[i].getAnnotations(), parameterNames[i]);
        }

        return methodParameters;
    }

    private String[] getParameterNames(Executable executable) {
        if(executable instanceof Method) {
            return nameDiscoverer.getParameterNames((Method) executable);
        } else if(executable instanceof Constructor) {
            return nameDiscoverer.getParameterNames((Constructor<?>) executable);
        } else {
            throw new IllegalArgumentException("parameter executable must be a constructor or method");
        }
    }
}
