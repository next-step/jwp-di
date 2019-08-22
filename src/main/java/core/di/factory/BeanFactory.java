package core.di.factory;

import com.google.common.collect.Maps;
import core.di.exception.BeanCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public BeanFactory() {
    }

    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstanticateBeans.forEach(this::registerBean);
    }

    private void initBean(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (injectedConstructor == null) {
            beans.put(clazz, clazz.newInstance());
            return;
        }


        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();

        Class<?>[] parameterConcreteClasses = findConcreteClasses(parameterTypes);

        for (Class<?> childClass : parameterConcreteClasses) {
            initBean(childClass);
        }

        Object[] classes = Arrays.stream(parameterConcreteClasses)
                .map(aClass -> beans.get(aClass))
                .toArray();

        beans.put(clazz, injectedConstructor.newInstance(classes));
    }

    private Class[] findConcreteClasses(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(aClass -> BeanFactoryUtils.findConcreteClass(aClass, preInstanticateBeans))
                .toArray(Class[]::new);
    }

    public void registerBean(Class<?> targetBean) {
        try {
            initBean(targetBean);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error("bean creation failed. ", e);
            throw new BeanCreationException();
        }
    }
}
