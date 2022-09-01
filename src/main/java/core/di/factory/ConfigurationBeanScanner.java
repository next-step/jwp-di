package core.di.factory;

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

public class ConfigurationBeanScanner {

    private static final Map<Class<?>, Object> CONFIGURATION_INSTANCES = new HashMap<>();
    private final Set<Class<?>> configurationClasses;

    public ConfigurationBeanScanner(final Set<Class<?>> configurationClasses) {
        this.configurationClasses = configurationClasses;
    }

    public void scan(final BeanFactory beanFactory) {
        final Set<Method> methods = ReflectionUtils.getMethodsAnnotatedWith(configurationClasses, Bean.class);

        methods.stream()
            .sorted(Comparator.comparingInt(Method::getParameterCount))
            .forEach(method -> addBean(beanFactory, method));
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
