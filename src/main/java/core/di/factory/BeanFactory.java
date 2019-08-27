package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;
import support.exception.NoSuchDefaultConstructorException;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
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
        for (Class<?> clazz : preInstanticateBeans) {
            addBean(clazz);
        }
    }

    private void addBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return;
        }

        instantiateClass(clazz);
    }

    private Object instantiateClass(Class<?> clazz) {
        Constructor<?> constructor = getConstructor(clazz);
        Class<?>[] parameterTypes = getParameterTypes(constructor);
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = instantiateClass(parameterTypes[i]);
        }

        Object object = instantiateConstructor(constructor, parameters);
        beans.put(clazz, object);
        return object;
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        return getDefaultConstructor(clazz);
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            throw new NoSuchDefaultConstructorException();
        }
    }

    private Class<?>[] getParameterTypes(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Class<?>[] concreteClass = new Class[parameterTypes.length];
        for (int i = 0; i < concreteClass.length; i++) {
            concreteClass[i] = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstanticateBeans);
        }

        return concreteClass;
    }

    private Object instantiateConstructor(Constructor<?> constructor, Object[] parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage());
            throw new CreateInstanceFailException();
        }
    }
}
