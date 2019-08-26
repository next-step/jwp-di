package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public static BeanFactory initialize(Set<Class<?>> preInstanticateBeans) {
        BeanFactory beanFactory = new BeanFactory(preInstanticateBeans);
        beanFactory.initialize();
        return beanFactory;
    }

    private void initialize() {
        preInstanticateBeans
                .forEach(this::initializeBean);
        logger.debug("Complete BeanFactory : {}", beans);
    }

    private void initializeBean(Class clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            beans.computeIfAbsent(clazz, BeanUtils::instantiateClass);
            return;
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        beans.put(clazz, BeanUtils.instantiateClass(injectedConstructor, getConstructorParameters(parameterTypes)));
    }

    private Object[] getConstructorParameters(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(clazz -> BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans))
                .peek(this::initializeBean)
                .map(this::getBean)
                .toArray();
    }

    @SuppressWarnings("unchecked")
    <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotations) {
        return beans.keySet()
                .stream()
                .filter(aClass -> aClass.isAnnotationPresent(annotations))
                .collect(Collectors.toMap(Function.identity(), beans::get));
    }
}