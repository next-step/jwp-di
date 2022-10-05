package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.*;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ConfigurationBeanScanner(final BeanFactory beanFactory, final Object... basePackage) {
        initialize(beanFactory, basePackage);
        this.beanFactory = beanFactory;
    }

    private void initialize(BeanFactory beanFactory, Object[] basePackage) {
        final var reflections = new Reflections(basePackage);
        final var configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);
        initial(configurationClasses, beanFactory);
    }

    public void register(Class<?> configClass) {
        final Set<Class<?>> classes = Sets.newHashSet();
        classes.add(configClass);
        initial(classes, beanFactory);
    }

    private void initial(Set<Class<?>> classes, BeanFactory beanFactory) {
        registerBeans(beanFactory, getBeanMethods(classes));
    }

    private List<Method> getBeanMethods(Set<Class<?>> configurationClasses) {
        return configurationClasses.stream()
                .flatMap(aClass -> Stream.of(aClass.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toList());
    }

    private void registerBeans(BeanFactory beanFactory, List<Method> beanMethods) {
        beanMethods.stream()
                .sorted(Comparator.comparingInt(Method::getParameterCount))
                .map(method -> invoke(
                        method,
                        beanFactory.instanticate(method.getDeclaringClass()),
                        createParameterObjects(beanFactory, method))
                )
                .forEach(bean -> beanFactory.setBean(bean.getClass(), bean));
    }

    private static Object[] createParameterObjects(final BeanFactory beanFactory, final Method method) {
        return Arrays.stream(method.getParameterTypes())
                .map(beanFactory::getBean)
                .toArray();
    }

    private Object invoke(Method method, Object configurationClass, Object... args) {
        try {
            return method.invoke(configurationClass, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
