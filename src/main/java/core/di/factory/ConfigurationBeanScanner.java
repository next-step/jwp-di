package core.di.factory;

import core.annotation.Configuration;
import core.di.factory.bean.Bean;
import core.di.factory.bean.ClassBean;
import core.di.factory.bean.MethodBean;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {

    private final Reflections reflections;

    public ConfigurationBeanScanner(Object... basePackages) {
        this.reflections = new Reflections(basePackages, Scanners.TypesAnnotated);
    }

    Collection<Bean> scan() {
        Collection<Class<?>> configurations = getConfiguration();
        Collection<Bean> beanConstructors = new ArrayList<>();
        beanConstructors.addAll(getConfigurationBeansAtClass(configurations));
        beanConstructors.addAll(getConfigurationBeansAtMethod(configurations));
        return Collections.unmodifiableCollection(beanConstructors);
    }

    public Collection<Class<?>> getConfiguration() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }
    private Collection<Bean> getConfigurationBeansAtClass(Collection<Class<?>> configurations) {
        return configurations.stream()
                .map(ClassBean::new)
                .collect(Collectors.toList());
    }

    private  Collection<Bean> getConfigurationBeansAtMethod(Collection<Class<?>> configurations) {
        return configurations.stream()
                .flatMap(configuration -> Stream.of(configuration.getMethods()))
                .filter(method -> method.isAnnotationPresent(core.annotation.Bean.class))
                .map(MethodBean::new)
                .collect(Collectors.toSet());
    }

}
