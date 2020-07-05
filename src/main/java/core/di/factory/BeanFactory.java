package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.exception.JwpException;
import core.exception.JwpExceptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        initialize(preInstanticateBeans);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        return getBeansAnnotationWith(Controller.class);
    }

    private Map<Class<?>, Object> getBeansAnnotationWith(Class<? extends Annotation> annotationClass) {
        return this.beans.entrySet().stream()
                .filter(beanEntry -> beanEntry.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void initialize(Set<Class<?>> preInstanticateBeans) {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            if (beans.containsKey(preInstanticateBean)) {
                continue;
            }
            beans.put(preInstanticateBean, getInstance(preInstanticateBean, preInstanticateBeans));
        }
    }

    private Object getInstance(Class<?> classType, Set<Class<?>> preInstanticateBeans) {
        if (beans.containsKey(classType)) {
            return beans.get(classType);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(classType);
        if (Objects.nonNull(constructor)) {
            beans.put(classType, getConstructorNewInstance(constructor, preInstanticateBeans));
            return beans.get(classType);
        }

        Class<?> clazz = BeanFactoryUtils.findConcreteClass(classType, preInstanticateBeans);
        return BeanUtils.instantiateClass(clazz);
    }

    private Object getConstructorNewInstance(Constructor<?> constructor, Set<Class<?>> preInstanticateBeans) {
        try {
            if (constructor.getParameterCount() > 0) {
                return constructor.newInstance(getInitArgs(constructor, preInstanticateBeans));
            }
            return constructor.newInstance();

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new JwpException(JwpExceptionStatus.CONSTRUCTOR_NEW_INSTANCE_FAIL, e);
        }
    }

    private Object[] getInitArgs(Constructor<?> constructor, Set<Class<?>> preInstanticateBeans) {
        Object[] initArgs = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            initArgs[i] = getInstance(constructor.getParameterTypes()[i], preInstanticateBeans);
        }
        return initArgs;
    }
}
