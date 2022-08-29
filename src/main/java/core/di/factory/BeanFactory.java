package core.di.factory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BeanFactory {
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
        for (Class<?> bean : preInstanticateBeans) {
            instantiateClass(bean);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> constructor = getConstructor(clazz);
        Object instance = instantiateConstructor(constructor);
        beans.put(clazz, instance);
        return instance;
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : constructor.getParameterTypes()) {
            args.add(instantiateClass(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans)));
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (constructor == null) {
                return clazz.getConstructor();
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not Found Constructor");
        }
    }
}
