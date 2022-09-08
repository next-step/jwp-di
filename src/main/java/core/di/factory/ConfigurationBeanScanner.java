package core.di.factory;

import static java.util.stream.Collectors.*;

import core.annotation.Bean;
import core.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    private static final Map<Class<?>, Object> CONFIGURATION_INSTANCES = new HashMap<>();
    private final Set<Class<?>> configurationClasses;
    private final Set<Method> methods;

    public ConfigurationBeanScanner(final Set<Class<?>> configurationClasses) {
        this.configurationClasses = configurationClasses;
        this.methods = ReflectionUtils.getMethodsAnnotatedWith(configurationClasses, Bean.class);
    }

    public void scan(final BeanFactory beanFactory) {
        methods.stream()
            .sorted(Comparator.comparingInt(Method::getParameterCount))
            .forEach(method -> addBean(beanFactory, method));
    }

    public Map<Class<?>, Object> scan2(final BeanFactory beanFactory) {
        return methods.stream()
            .collect(
                HashMap::new,
                (map, method) -> map.put(method.getReturnType(), initiateBean2(beanFactory, method)),
                HashMap::putAll
            );
    }

    private Object initiateBean(final BeanFactory beanFactory, final Method method) {
        final Object[] arguments = getArguments(beanFactory, method.getParameters());
        final Object instance = getInstance(method);

        return ReflectionUtils.invokeMethod(instance, method, arguments);
    }

    private Object initiateBean2(final BeanFactory beanFactory, final Method method) {
        final Object[] arguments = getArguments2(beanFactory, method.getParameters());
        final Object instance = getInstance(method);

        return ReflectionUtils.invokeMethod(instance, method, arguments);
    }

    private void addBean(final BeanFactory beanFactory, final Method method) {
        final Class<?> returnType = method.getReturnType();
        Object[] arguments = getArguments(beanFactory, method.getParameters());
        Object instance = getInstance(method);

        final Object bean = ReflectionUtils.invokeMethod(instance, method, arguments);
        beanFactory.addBean(returnType, bean);
    }

    private Object[] getArguments(final BeanFactory beanFactory, final Parameter[] parameters) {
        return Arrays.stream(parameters)
            .map(parameter -> beanFactory.getBean(parameter.getType()))
            .toArray();
    }

    private Object[] getArguments2(final BeanFactory beanFactory, final Parameter[] parameters) {

        return Arrays.stream(parameters)
            .filter(this::hasSameTypeOfParameter)
            .findAny()
            .map(parameter -> getOrInitiateBean(beanFactory, parameter))
            .stream()
            .toArray();
    }

    private boolean hasSameTypeOfParameter(final Parameter parameter) {
        return methods.stream()
            .map(Method::getReturnType)
            .anyMatch(parameter.getType()::equals);
    }

    private Object getOrInitiateBean(final BeanFactory beanFactory, final Parameter parameter) {
        final Object bean = beanFactory.getBean(parameter.getType());

        if (Objects.isNull(bean)) {
            return methods.stream()
                .filter(method -> parameter.getType().equals(method.getReturnType()))
                .findAny()
                .map(method -> initiateBean(beanFactory, method))
                .orElseThrow(() -> new IllegalArgumentException("Bean not found"));
        }
        return bean;
    }

    private Object getInstance(final Method method) {
        final Class<?> declaringClass = method.getDeclaringClass();

        if (CONFIGURATION_INSTANCES.containsKey(declaringClass)) {
            return CONFIGURATION_INSTANCES.get(declaringClass);
        }

        return CONFIGURATION_INSTANCES.computeIfAbsent(declaringClass, ReflectionUtils::newInstance);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConfigurationBeanScanner that = (ConfigurationBeanScanner) o;
        return Objects.equals(configurationClasses, that.configurationClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configurationClasses);
    }
}
