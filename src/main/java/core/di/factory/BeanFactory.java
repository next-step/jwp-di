package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            beans.put(preInstanticateBean, instanticate(preInstanticateBean));
        }
    }

    Object instanticate(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (injectedConstructor == null) {
            return instantiateClass(clazz);
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(instanticate(parameterType));
        }
        return instantiateConstructor(injectedConstructor, parameters);
    }

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreteChildClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        return BeanUtils.instantiateClass(concreteChildClass);
    }

    private Object instantiateConstructor(Constructor<?> constructor, List<Object> parameters)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(parameters.toArray());
    }
}
