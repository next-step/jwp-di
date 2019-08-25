package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import core.annotation.web.Controller;
import core.mvc.tobe.BeanScanner;
import org.springframework.beans.BeanUtils;
import support.exception.ExceptionWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {

    private final BeanScanner beanScanner;
    private final Set<Class<?>> annotatedTypes;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(final BeanScanner beanScanner) {
        this.beanScanner = beanScanner;
        this.annotatedTypes = beanScanner.getTypesAnnotatedWith();
    }

    public void initialize() {
        beanScanner.getAnnotations()
                .forEach(this::createBeans);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Set<Class<?>> getControllers() {
        return annotatedTypes.stream()
                .filter(type -> type.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

    private void createBeans(Class<? extends Annotation> annotation) {
        annotatedTypes.stream()
                .filter(bean -> bean.isAnnotationPresent(annotation))
                .forEach(ExceptionWrapper.consumer(
                        bean -> {
                            final Constructor<?> injectedConstructor = getInjectedConstructor(bean);
                            final Object[] parameterBeans = getParameterBeans(injectedConstructor);

                            beans.put(bean, BeanUtils.instantiateClass(injectedConstructor, parameterBeans));
                        }));
    }

    private Constructor<?> getInjectedConstructor(Class<?> bean) throws NoSuchMethodException {
        final Constructor<?> injectedConstructor = Arrays.stream(bean.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .max(Comparator.comparing(Constructor::getParameterCount))
                .orElse(null);

        if (injectedConstructor == null) {
            return bean.getDeclaredConstructor();
        }

        return injectedConstructor;
    }

    private Object[] getParameterBeans(Constructor<?> injectedConstructor) {
        return Arrays.stream(injectedConstructor.getParameterTypes())
                .map(ExceptionWrapper.function(this::getObjectFromBeans))
                .toArray();
    }

    private Object getObjectFromBeans(Class<?> parameter) throws ClassNotFoundException {
        return beans.values().stream()
                .filter(value -> parameter.isAssignableFrom(value.getClass()))
                .findFirst()
                .orElseThrow(ClassNotFoundException::new);
    }
}
