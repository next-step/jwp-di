package core.di.factory;

import com.google.common.collect.Maps;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (final Class<?> bean : preInstanticateBeans) {
            final Object instance = beans.computeIfAbsent(bean, this::getInstance);
            beans.put(bean, instance);

            logger.debug("added bean in factory: {}", instance.getClass().getName());
        }
    }

    private Object getInstance(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        final Constructor<?> constructor = getInjectedConstructor(clazz);

        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] arguments = createArguments(parameterTypes);

        return ReflectionUtils.newInstance(constructor, arguments);
    }

    private Object[] createArguments(final Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
            .map(parameterType -> getInstance(BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans)))
            .toArray();
    }

    private static Constructor<?> getInjectedConstructor(final Class<?> clazz) {
        final Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor != null) {
            return injectedConstructor;
        }

        return ReflectionUtils.getConstructorByArgs(clazz);
    }
}
