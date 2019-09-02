package core.di.factory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for(Class<?> beanType : preInstanticateBeans) {
            this.addBean(beanType);
        }
    }

    private Object addBean(Class<?> beanType) {
        final Constructor<?> constructor = getConstructor(beanType);
        Object[] parameterBeans = getParameters(constructor);
        Object instance = createInstance(constructor, parameterBeans);
        beans.put(beanType, instance);
        return instance;
    }

    private Object[] getParameters(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterBeans = new Object[parameterTypes.length];
        if(parameterBeans.length == 0) {
            return parameterBeans;
        }

        int indexOfArgs = 0;
        for(Class<?> parameterType : parameterTypes) {
            parameterBeans[indexOfArgs] = getParameterInstance(parameterType);
            indexOfArgs++;
        }
        return parameterBeans;
    }

    private Object getParameterInstance(Class<?> parameterType) {
        final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
        return beans.containsKey(concreteClass)
                ? beans.get(concreteClass)
                : this.addBean(concreteClass);
    }

    private Constructor<?> getConstructor(Class<?> beanType) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(beanType);
        if(constructor != null) {
            return constructor;
        }
        try {
            return beanType.getConstructor();
        } catch (NoSuchMethodException ex) {
            throw new CannotFoundConstructorException(ex);
        }
    }

    private Object createInstance(Constructor<?> constructor, Object... parameterBeans) {
        try {
            return constructor.newInstance(parameterBeans);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new CannotNewInstanceException(ex);
        }
    }
}
