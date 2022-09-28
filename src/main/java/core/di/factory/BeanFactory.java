package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElseGet;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);


    private final BeanScanner scanner;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(BeanScanner scanner) {
        this.scanner = scanner;
    }
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        scanner.scan()
                .forEach(this::createInstance);
    }

    private Object createInstance(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Object instance = instantiateClass(clazz);
        beans.put(clazz, instance);
        return instance;
    }

    private Object instantiateClass(Class<?> clazz) {
        if (clazz.isInterface()) {
            return createInstance(scanner.subTypeOf(clazz));
        }

        Constructor<?> constructor = requireNonNullElseGet(BeanFactoryUtils.getInjectedConstructor(clazz), () -> defaultConstructor(clazz));
        return BeanUtils.instantiateClass(constructor, getParameters(constructor));
    }

    private Constructor<?> defaultConstructor(Class<?> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(clazz.getName(), e);
        }
    }

    private Object[] getParameters(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(this::createInstance)
                .toArray();
    }

    public Map<Class<?>, Object> annotatedWith(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
