package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;

        preInstanticateBeans.forEach(
            clazz -> log.debug("preInstanticateBeans: {}", clazz.getSimpleName())
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            beans.put(beanClass, getBeanInstance(beanClass));
        }
        beans.keySet().forEach(clazz -> log.debug("beanClassName: {}", clazz.getSimpleName()));
    }

    private Object getBeanInstance(Class<?> beanClass) {
        Constructor constructor = BeanFactoryUtils.getInjectedConstructors(beanClass);

        try {
            if (hasNoArgument(constructor)) {
                return getNoArgBeanInstance(beanClass, constructor);
            }

            return getArgBeanInstance(constructor);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private boolean hasNoArgument(Constructor constructor) {
        return Objects.isNull(constructor) || ArrayUtils.isEmpty(constructor.getParameters());
    }

    private Object getNoArgBeanInstance(Class<?> beanClass, Constructor constructor) {
        if (beans.containsKey(beanClass)) {
            return beans.get(beanClass);
        }

        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(beanClass);
        }

        return BeanUtils.instantiateClass(constructor);
    }

    private Object getArgBeanInstance(Constructor constructor) {
        Parameter[] parameters = constructor.getParameters();
        List<Object> arguments = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);

            if (beans.containsKey(concreteClass)) {
                arguments.add(beans.get(concreteClass));
            }
            else {
                Object beanInstance = getBeanInstance(concreteClass);
                beans.put(concreteClass, beanInstance);
                arguments.add(beanInstance);
            }
        }

        return BeanUtils.instantiateClass(constructor, arguments.toArray(new Object[0]));
    }
}

