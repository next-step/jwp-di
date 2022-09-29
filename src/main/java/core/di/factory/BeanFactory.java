package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

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
        preInstanticateBeans.forEach(clazz -> createBean(clazz));
        logger.debug("[Beans created]: " + beans.keySet());
    }

    public Map<Class<?>, Object> getControllers() {
        return beans.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object createBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        final Class<?> concreteClass = findConcreteClass(clazz, preInstanticateBeans);

        final Constructor<?> injectedConstructor = getInjectedConstructor(concreteClass);
        if (injectedConstructor == null) {
            return createBeanNotInjected(concreteClass);
        }
        return createBeanInjected(concreteClass, injectedConstructor);
    }

    private Object createBeanNotInjected(Class<?> concreteClass){
        final Object bean = BeanUtils.instantiateClass(concreteClass);
        beans.put(concreteClass, bean);
        return bean;
    }

    private Object createBeanInjected(Class<?> concreteClass, Constructor<?> injectedConstructor) {
        final Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        final Object[] args = Arrays.stream(parameterTypes)
                .map(type -> createBean(type))
                .toArray();

        final Object bean = BeanUtils.instantiateClass(injectedConstructor, args);

        beans.put(concreteClass, bean);
        return bean;
    }
}
