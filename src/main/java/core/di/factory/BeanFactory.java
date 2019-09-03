package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        for(Class<?> beanType : preInstanticateBeans) {
            Object instance = this.createBean(beanType);
            this.addBean(beanType, instance);
        }
    }

    Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return this.beans.entrySet().stream()
                .filter(bean -> bean.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object createBean(Class<?> beanType) {
        final Constructor<?> constructor = getConstructor(beanType);
        Object[] parameterBeans = getParameters(constructor);
        return createInstance(constructor, parameterBeans);
    }

    private Object[] getParameters(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterBeans = new Object[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstanticateBeans);
            parameterBeans[i] = getOrCreateBean(concreteClass);
        }
        return parameterBeans;
    }

    private Object getOrCreateBean(Class<?> concreteClass) {
        if(beans.containsKey(concreteClass)) {
            return beans.get(concreteClass);
        }
        final Object instance = this.createBean(concreteClass);
        this.addBean(concreteClass, instance);
        return instance;
    }

    private Constructor<?> getConstructor(Class<?> beanType) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(beanType);
        if(constructor != null) {
            return constructor;
        }
        try {
            return beanType.getConstructor();
        } catch (NoSuchMethodException ex) {
            throw new CannotFoundConstructorException(ex);
        }
    }

    private Object createInstance(Constructor<?> constructor, Object... parameterBeans) {
        try {
            return constructor.newInstance(parameterBeans);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new CannotNewInstanceException(ex);
        }
    }

    private void addBean(Class<?> beanType, Object bean) {
        beans.put(beanType, bean);
    }
}
