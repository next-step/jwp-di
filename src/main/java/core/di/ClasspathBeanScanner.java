package core.di;

import core.annotation.Component;
import core.di.factory.BeanFactoryUtils;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

public class ClasspathBeanScanner {
    private static final String BASE_ANNOTATION = Component.class.getPackage().getName();

    public Set<BeanDefinition> scan(Set<String> basePackages) {
        Reflections reflections = new Reflections(BASE_ANNOTATION, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(Component.class);
        annotations.add(Component.class);

        Set<Class<?>> beanClasses = getTypesAnnotatedWith(basePackages, annotations);
        beanClasses = beanClasses.stream()
            .filter(clazz -> !clazz.isInterface())
            .collect(Collectors.toSet());

        return createBeanDefinitions(beanClasses);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Set<String> basePackages, Set<Class<?>> annotations) {
        Reflections reflections = new Reflections(basePackages.toArray(), new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> classes = new HashSet<>();
        for (Class annotation : annotations) {
            classes.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return classes;
    }

    private Set<BeanDefinition> createBeanDefinitions(Set<Class<?>> beanClasses) {
        return beanClasses.stream()
            .map(this::createBeanDefinition)
            .collect(Collectors.toSet());
    }

    private BeanDefinition createBeanDefinition(Class<?> clazz) {
        Component component = AnnotationUtils.findAnnotation(clazz, Component.class);
        String name = StringUtils.isEmpty(component.value()) ? clazz.getSimpleName() : component.value();
        return new BeanDefinition() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Method getMethod() {
                return null;
            }

            @Override
            public Constructor getConstructor() {
                return BeanFactoryUtils.getInjectedConstructor(clazz)
                    .orElseGet(() -> ReflectionUtils.getConstructorByArgs(clazz));
            }

            @Override
            public Class<?> getBeanClass() {
                return clazz;
            }
        };
    }
}
