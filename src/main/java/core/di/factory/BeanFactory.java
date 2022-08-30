package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
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
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            instantiateBean(preInstanticateBean);
        }
    }

    private Object instantiateBean(Class<?> preInstanticateBean) {
        if (beans.containsKey(preInstanticateBean)) {
            return beans.get(preInstanticateBean);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstanticateBean);

        if (Objects.isNull(injectedConstructor)) {
            Object result = BeanUtils.instantiateClass(preInstanticateBean);
            beans.put(preInstanticateBean, result);
            return result;
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteType = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstanticateBeans);
            parameters[i] = instantiateBean(concreteType);
        }

        Object object = BeanUtils.instantiateClass(injectedConstructor, parameters);
        beans.put(preInstanticateBean, object);
        return object;
    }
}
