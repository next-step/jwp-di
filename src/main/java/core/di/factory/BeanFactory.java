package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeanClazzs;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeanClazzs) {
        this.preInstantiateBeanClazzs = preInstantiateBeanClazzs;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> clazz : preInstantiateBeanClazzs) {
            Object bean = instantiateBean(clazz);
            beans.put(clazz, bean);
        }
    }

    private Object instantiateBean(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeanClazzs);
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(concreteClass);
        }

        try {
            Object[] params = Arrays.stream(constructor.getParameters())
                    .map(param -> instantiateBean(param.getType()))
                    .toArray();
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException("fail to initiate clazz for bean: " + clazz, e);
        }
    }
}
