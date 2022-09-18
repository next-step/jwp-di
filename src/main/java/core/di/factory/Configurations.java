package core.di.factory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Configurations {

    private final Map<Class<?>, Class<?>> beanByConfiguration;

    public Configurations(Set<Class<?>> values) {
        this.beanByConfiguration = values.stream()
            .flatMap(config -> Arrays.stream(config.getDeclaredMethods())
                .map(method -> Map.entry(method.getReturnType(), config)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    public Set<Class<?>> findAllBeans() {
        return beanByConfiguration.keySet();
    }

    public boolean hasBean(Class<?> target) {
        return beanByConfiguration.keySet()
            .contains(target);
    }

    public Class<?> findConfiguration(Class<?> bean) {
        return beanByConfiguration.get(bean);
    }
}
