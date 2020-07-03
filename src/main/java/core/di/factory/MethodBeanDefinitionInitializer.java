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
public class MethodBeanDefinitionInitializer implements BeanInitializer {

    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition instanceof MethodBeanDefinition;
    }

    @Nullable
    @Override
    public Object instantiate(BeanDefinition definition, BeanFactory beanFactory) {
        if(!support(definition)) {
            return null;
        }

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

    private Object[] getParameters(BeanFactory beanFactory, MethodParameter[] methodParameters) {
        Object[] parameters = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            String beanName = getBeanName(methodParameters[i]);
            parameters[i] = beanFactory.getBean(beanName, methodParameters[i].getType());
        }

        return parameters;
    }

    private String getBeanName(MethodParameter methodParameter) {
        String name = methodParameter.getType().getName();
        Qualifier qualifier = methodParameter.getAnnotation(Qualifier.class);

        return qualifier == null ? name : qualifier.value();
    }

    private MethodParameter[] getMethodParameters(Method method) {
        MethodParameter[] methodParameters = new MethodParameter[method.getParameters().length];
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = new MethodParameter(method, parameters[i].getType(), parameters[i].getAnnotations(), parameterNames[i]);
        }

        return methodParameters;
    }
}

//@Override
//    public Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory) {
//        if(!support(beanDefinition)) {
//            return null;
//        }
//
//        Constructor constructor = findInjectController(beanDefinition.getType());
//
//        if(constructor == null) {
//            throw new BeanInstantiationException(beanDefinition.getType(), "Constructor with @Inject not Found");
//        }
//
//        MethodParameter[] methodParameters = getMethodParameters(constructor);
//        Object[] parameters = getParameters(beanFactory, methodParameters);
//
//        Object instance = BeanUtils.instantiateClass(constructor, parameters);
//        logger.info("bean " + instance.getClass() + " instantiate");
//
//        return instance;
//    }
//
//    private Object[] getParameters(BeanFactory beanFactory, MethodParameter[] methodParameters) {
//        Object[] parameters = new Object[methodParameters.length];
//
//        for (int i = 0; i < methodParameters.length; i++) {
//            String beanName = getBeanName(methodParameters[i]);
//            parameters[i] = beanFactory.getBean(beanName, methodParameters[i].getType());
//        }
//
//        return parameters;
//    }
//
//    private String getBeanName(MethodParameter methodParameter) {
//        String name = methodParameter.getType().getName();
//        Qualifier qualifier = methodParameter.getAnnotation(Qualifier.class);
//
//        return qualifier == null ? name : qualifier.value();
//    }
//
//    private MethodParameter[] getMethodParameters(Constructor constructor) {
//        MethodParameter[] methodParameters = new MethodParameter[constructor.getParameters().length];
//        String[] parameterNames = nameDiscoverer.getParameterNames(constructor);
//        Parameter[] parameters = constructor.getParameters();
//
//        for (int i = 0; i < parameters.length; i++) {
//            methodParameters[i] = new MethodParameter(constructor, parameters[i].getType(), parameters[i].getAnnotations(), parameterNames[i]);
//        }
//
//        return methodParameters;
//    }
//
//    private Constructor findInjectController(Class<?> targetClass) {
//        for (Constructor<?> constructor : targetClass.getConstructors()) {
//            if(constructor.isAnnotationPresent(Inject.class)) {
//                return constructor;
//            }
//        }
//
//        try {
//            return targetClass.getConstructor();
//        } catch (NoSuchMethodException e) {
//            return null;
//        }
//    }
//
//}