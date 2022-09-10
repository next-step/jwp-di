package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private static final Map<Class<?>, Object> CONFIGURATION_INSTANCES = new HashMap<>();

    private final Set<Class<?>> preInstanticateBeans;
    private final Set<Method> preInstanticateMethodBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
        this.preInstanticateBeans = new HashSet<>();
        this.preInstanticateMethodBeans = new HashSet<>();
    }

    public void addPreInstanticateBeans(Class<?>... classes) {
        preInstanticateBeans.addAll(Arrays.asList(classes));
    }

    public void addPreInstanticateMethodBeans(Set<Method> methods) {
        preInstanticateMethodBeans.addAll(methods);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (final Method method : preInstanticateMethodBeans) {
            final Object methodBeanInstance = beans.computeIfAbsent(method.getReturnType(), key -> getInstance(method));
            logger.debug("added configuration bean in factory: {}", methodBeanInstance.getClass().getName());
        }
        for (final Class<?> bean : preInstanticateBeans) {
            final Object instance = beans.computeIfAbsent(bean, this::getInstance);
            logger.debug("added component bean in factory: {}", instance.getClass().getName());
        }
    }

    private Object getInstance(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        logger.debug("create bean: {}", clazz.getName());
        final Constructor<?> constructor = getInjectedConstructor(clazz);

        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] arguments = createArguments(parameterTypes);

        return ReflectionUtils.newInstance(constructor, arguments);
    }

    private Object getInstance(Method method) {
        if (beans.containsKey(method.getReturnType())) {
            return beans.get(method.getReturnType());
        }

        final Object configurationInstance = getConfigurationInstance(method);
        final Object[] arguments = createMethodArguments(method.getParameterTypes());

        return ReflectionUtils.invokeMethod(configurationInstance, method, arguments);
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

    private Object[] createMethodArguments(final Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
            .map(parameterType -> getInstance(BeanFactoryUtils.findConcreteMethod(parameterType, preInstanticateMethodBeans)))
            .toArray();
    }

    public Set<Class<?>> getControllerTypes() {
        return beans.keySet().stream()
            .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
            .collect(Collectors.toUnmodifiableSet());
    }

    private Object getConfigurationInstance(final Method method) {
        final Class<?> declaringClass = method.getDeclaringClass();

        if (CONFIGURATION_INSTANCES.containsKey(declaringClass)) {
            return CONFIGURATION_INSTANCES.get(declaringClass);
        }

        return CONFIGURATION_INSTANCES.computeIfAbsent(declaringClass, ReflectionUtils::newInstance);
    }
}
