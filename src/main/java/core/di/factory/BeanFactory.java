package core.di.factory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNullElseGet;

public class BeanFactory {

    private final BeanScanner scanner;
    private final Map<Class<?>, Object> beans = new HashMap<>();

    private BeanFactory(BeanScanner scanner) {
        Assert.notNull(scanner, "scanner must not be null");
        this.scanner = scanner;
    }

    public static BeanFactory from(BeanScanner scanner) {
        return new BeanFactory(scanner);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        scanner.scan()
                .forEach(this::bean);
    }

    public Map<Class<?>, Object> annotatedWith(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object bean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }
        Object instance = instantiateClass(clazz);
        beans.put(clazz, instance);
        return instance;
    }

    private Object instantiateClass(Class<?> clazz) {
        if (clazz.isInterface()) {
            return bean(scanner.subTypeOf(clazz));
        }
        Constructor<?> constructor = requireNonNullElseGet(BeanFactoryUtils.getInjectedConstructor(clazz), () -> defaultConstructor(clazz));
        return BeanUtils.instantiateClass(constructor, arguments(constructor));
    }

    private Object[] arguments(Constructor<?> constructor) {
        return Stream.of(constructor.getParameterTypes())
                .map(this::bean)
                .toArray();
    }

    private Constructor<?> defaultConstructor(Class<?> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(clazz.getName(), e);
        }
    }
}
