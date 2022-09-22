package core.di.factory.scanner;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.constructor.BeanConstructor;
import core.di.factory.constructor.ClassBeanConstructor;
import core.di.factory.constructor.MethodBeanConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {

    private final Reflections reflections;

    public ConfigurationBeanScanner(Object... basePackages) {
        Assert.notNull(basePackages, "basePackage가 null이어선 안됩니다.");
        this.reflections = new Reflections(basePackages, Scanners.TypesAnnotated);
    }

    public Collection<BeanConstructor> scan() {
        Collection<Class<?>> configurations = configurations();
        Collection<BeanConstructor> beanConstructors = new ArrayList<>();

        beanConstructors.addAll(classBeanConstructors(configurations));
        beanConstructors.addAll(methodBeanConstructors(configurations));
        return Collections.unmodifiableCollection(beanConstructors);
    }

    public Collection<Class<?>> configurations() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }

    private List<ClassBeanConstructor> classBeanConstructors(Collection<Class<?>> configurations) {
        return configurations.stream()
                .map(ClassBeanConstructor::new)
                .collect(Collectors.toList());
    }

    private Collection<MethodBeanConstructor> methodBeanConstructors(Collection<Class<?>> configurations) {
        return configurations.stream()
                .flatMap(configuration -> Stream.of(configuration.getMethods()))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(MethodBeanConstructor::new)
                .collect(Collectors.toSet());
    }
}
