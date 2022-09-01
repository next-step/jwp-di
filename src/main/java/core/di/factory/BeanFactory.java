package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
        this.preInstanticateBeans = new HashSet<>();
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public void addPreInstanticateBeans(Class<?>... classes) {
        preInstanticateBeans.addAll(Arrays.asList(classes));
    }

    public void addBean(Class<?> clazz, Object bean) {
        beans.put(clazz, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (final Class<?> bean : preInstanticateBeans) {
            final Object instance = beans.computeIfAbsent(bean, this::getInstance);
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

    private Constructor<?> getInjectedConstructor(final Class<?> clazz) {
        final Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor != null) {
            return injectedConstructor;
        }

        return ReflectionUtils.getConstructorByArgs(clazz);
    }

    private Object[] createArguments(final Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
            .map(parameterType -> getInstance(BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans)))
            .toArray();
    }

    public Set<Class<?>> getControllerTypes() {
        return beans.keySet().stream()
            .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
            .collect(Collectors.toUnmodifiableSet());
    }
}
