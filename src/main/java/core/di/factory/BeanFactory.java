package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import support.exception.ExceptionWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    public static final Class<? extends Annotation>[] ANNOTATIONS =
            new Class[] {Repository.class, Service.class, Controller.class};

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        Arrays.stream(ANNOTATIONS)
                .forEach(this::createBeans);
    }

    private void createBeans(Class<? extends Annotation> annotation) {
        preInstanticateBeans.stream()
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
