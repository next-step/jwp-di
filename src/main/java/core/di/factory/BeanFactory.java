package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    /**
     * if exist beans
     *  return bean
     * else
     *  createBean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.info("find {}", requiredType.getName());

        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }

        return createBean(requiredType);
    }

    @SuppressWarnings("unchecked")
    private <T> T createBean(Class<T> clazz) {
        try {
            Class<T> concreteClass = (Class<T>) findConcreteClass(clazz, preInstanticateBeans);
            T instance = newInstance(concreteClass);
            populateBean(instance, concreteClass);
            return instance;
        } catch (IllegalAccessException e) {
            logger.info("class access failed", e);
        } catch (InvocationTargetException e) {
            logger.info("target invocation failed", e);
        } catch (InstantiationException e) {
            logger.info("instantiation failed", e);
        }

        return null;
    }

    private <T> void populateBean(T instance, Class<T> clazz) throws IllegalAccessException {
        final Field[] fields = clazz.getFields();

        for (Field field : fields) {
            if (ClassUtils.isPrimitiveOrWrapper(field.getClass())) {
                continue;
            }

            Object bean = getBean(field.getClass());
            field.setAccessible(true);
            field.set(instance, bean);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T newInstance(Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<?> constructor = getInjectedConstructor(clazz);

        if (constructor == null) {
            constructor = clazz.getConstructors()[0];
            if (constructor.getParameterCount() != 0) {
                throw new IllegalStateException(clazz.getName() + " Default Constructor is Required");
            }
            return (T) constructor.newInstance();
        }

        Object[] args = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Class<?> parameterType = constructor.getParameterTypes()[i];
            args[i] = getBean(parameterType);
        }

        return (T) constructor.newInstance(args);
    }



    /**
     * 1. create instance
     *   - constructor injection
     * 2. populate bean
     *
     * getBean (Class)
     *   if null
     *     - createBean
     *   else
     *     - getBean
     *
     */
    @SuppressWarnings("unchecked")
    public void initialize() {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            Object bean = getBean(preInstanticateBean);
            if (bean == null) {
                logger.warn("{} initiate failed. ", preInstanticateBean.getName());
            }
        }
    }

    private boolean isInjectField(Field field) {
        return field != null && field.isAnnotationPresent(Inject.class);
    }
}
