package core.di.factory.generator;

import core.di.exception.BeanCreateException;
import core.di.exception.NoDefaultConstructorException;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanType;
import core.di.factory.MethodInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class MethodTypeGenerator extends AbstractBeanGenerator {
    @Override
    public boolean support(BeanInitInfo beanInitInfo) {
        return beanInitInfo.getBeanType() == BeanType.BEAN;
    }

    @Override
    Object generateBean(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo) {
        MethodInfo methodInfo = beanInitInfo.getMethodInfo();
        Method method = methodInfo.getMethod();
        Object instance = newInstance(methodInfo.getOriginClass());
        Object[] arguments = getArguments(dependency, beanFactory, method.getParameterTypes());

        return invoke(method, instance, arguments);
    }

    private Object invoke(Method method, Object instance, Object[] arguments) {
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreateException("Fail to create bean with @Bean annotation : " + method.getName());
        }
    }

    private Object newInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoDefaultConstructorException(clazz);
        }
    }
}
