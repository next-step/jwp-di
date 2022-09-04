package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try {
            for (Class<?> preInstanticateBean : preInstanticateBeans) {
                beans.put(preInstanticateBean, instantiateClass(preInstanticateBean));
            }
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage());
        }
    }

    private Object instantiateClass(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        logger.info("InstantiateClass : {}", clazz);
        if (beans.containsKey(clazz)) {
            logger.info("AlreadyInstantiateClass : {}", clazz);
            return beans.get(clazz);
        }

        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (injectedConstructor == null) {
            logger.info("NotExist InjectConstructor, Use Default Constructor : {}", concreteClass);
            return concreteClass.newInstance();
        }
        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = instantiateClass(parameterTypes[i]);
        }
        logger.info("Create Instance Used Constructor");
        return constructor.newInstance(parameters);
    }
}
