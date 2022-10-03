package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(beans.get(requiredType));
    }

    public <T> void setBean(Class<T> inputType, Object object) {
        beans.put(inputType, object);
    }

    public void initialize() {
        if (preInstanticateBeans == null || preInstanticateBeans.size() < 1) {
            return;
        }

        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            beans.put(preInstanticateBean, instanticate(preInstanticateBean));
        }
    }

    Object instanticate(Class<?> clazz) {
        try {
            return instantiateClass(clazz);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object instantiateClass(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (getBean(clazz) != null) {
            return getBean(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans));
        }

        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateConstructor(Constructor<?> constructor)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(instantiateClass(parameterType));
        }
        return BeanUtils.instantiateClass(constructor, parameters.toArray());
    }

    public Map<Class<?>, Object> getControllers() {
        return preInstanticateBeans.stream().filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(clazz -> clazz, beans::get));
    }

    public void setPreInstanticateBeans(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }
}
