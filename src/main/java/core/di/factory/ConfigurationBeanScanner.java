package core.di.factory;

import core.annotation.Bean;
import core.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    private final Set<Class<?>> configurationClasses;

    public ConfigurationBeanScanner(final Set<Class<?>> configurationClasses) {
        this.configurationClasses = configurationClasses;
    }

    public Set<Method> scan() {
        return ReflectionUtils.getMethodsAnnotatedWith(configurationClasses, Bean.class)
            .stream()
            .collect(Collectors.toUnmodifiableSet());
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
