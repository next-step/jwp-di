package core.di;

import core.annotation.Component;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

public class ClasspathBeanScanner {
    private static final String BASE_ANNOTATION = Component.class.getPackage().getName();

    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(Set<String> basePackages){
        Reflections reflections = new Reflections(BASE_ANNOTATION, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(Component.class);
        annotations.add(Component.class);

        Set<Class<?>> beanClasses = getTypesAnnotatedWith(basePackages, annotations);
        beanClasses = beanClasses.stream().filter(aClass -> !aClass.isInterface()).collect(Collectors.toSet());

        beanFactory.addBeanDefinitions(createBeanDefinitions(beanClasses));
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
        return beanClasses.stream().map(aClass -> {
            Component component = AnnotationUtils.findAnnotation(aClass, Component.class);
            String name = StringUtils.isEmpty(component.value())? aClass.getSimpleName() : component.value();
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
                    return BeanFactoryUtils.getInjectedConstructor(aClass)
                        .orElseGet(() -> ReflectionUtils.getConstructorByArgs(aClass));
                }

                @Override
                public Class<?> getBeanClass() {
                    return aClass;
                }
            };
        }).collect(Collectors.toSet());

    }
}
