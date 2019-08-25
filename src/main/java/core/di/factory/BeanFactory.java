package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        for (Class<?> clazz : preInstanticateBeans) {
            initBeans(clazz);
        }
    }

    private void initBeans(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        beans.put(clazz,getInstance(clazz,injectedConstructor));
    }

    private Object getInstance(Class<?> clazz, Constructor<?> injectedConstructor) {
        return injectedConstructor == null ? BeanUtils.instantiateClass(clazz) : getInjectInstance(injectedConstructor);
    }

    private Object getInjectInstance(Constructor<?> injectedConstructor) {
        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        List params = makeConstructorParams(parameterTypes);
        return BeanUtils.instantiateClass(injectedConstructor, params.stream().toArray());
    }

    private List makeConstructorParams(Class<?>[] parameterTypes) {
        List params = new ArrayList();
        for (Class clazz : parameterTypes) {
            params.add(getConcreteClass(clazz));
        }
        return params;
    }

    private Object getConcreteClass(Class clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        if(beans.get(concreteClass) == null) {
            initBeans(concreteClass);
        }
        return beans.get(concreteClass);
    }

}
