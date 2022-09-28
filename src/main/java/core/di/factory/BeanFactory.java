package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

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
        preInstanticateBeans.forEach(clazz -> generateBean(clazz));
        logger.debug("[Beans created]: " + beans.keySet());
    }

    private Object generateBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        final Class<?> concreteClass = findConcreteClass(clazz, preInstanticateBeans);

        final Constructor<?> injectedConstructor = getInjectedConstructor(concreteClass);
        if (injectedConstructor == null) {
            final Object bean = BeanUtils.instantiateClass(concreteClass);
            beans.put(concreteClass, bean);
            return bean;
        }

        final Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        final Object[] args = Arrays.stream(parameterTypes)
                .map(type -> generateBean(type))
                .toArray();
        final Object bean = BeanUtils.instantiateClass(injectedConstructor, args);
        beans.put(concreteClass, bean);
        return bean;
    }
}
