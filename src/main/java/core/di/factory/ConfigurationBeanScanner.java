package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.constructor.BeanConstructor;
import core.di.factory.constructor.ClassBeanConstructor;
import core.di.factory.constructor.MethodBeanConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ConfigurationBeanScanner {

    private final Reflections reflections;

    private ConfigurationBeanScanner(Object... basePackages) {
        Assert.notNull(basePackages, "basePackage must not be null");
        this.reflections = new Reflections(basePackages, Scanners.TypesAnnotated);
    }

    static ConfigurationBeanScanner packages(Object... basePackages) {
        return new ConfigurationBeanScanner(basePackages);
    }

    Collection<BeanConstructor> scan() {
        Collection<Class<?>> configurations = configurations();
        Collection<BeanConstructor> beanConstructors = new ArrayList<>();
        beanConstructors.addAll(classBeanConstructors(configurations));
        beanConstructors.addAll(methodBeanConstructors(configurations));
        return Collections.unmodifiableCollection(beanConstructors);
    }

    Collection<Class<?>> configurations() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }

    private List<ClassBeanConstructor> classBeanConstructors(Collection<Class<?>> configurations) {
        return configurations.stream()
                .map(ClassBeanConstructor::from)
                .collect(Collectors.toList());
    }

    private Collection<MethodBeanConstructor> methodBeanConstructors(Collection<Class<?>> configurations) {
        return configurations.stream()
                .flatMap(configuration -> Stream.of(configuration.getMethods()))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(MethodBeanConstructor::from)
                .collect(Collectors.toSet());
    }
}
