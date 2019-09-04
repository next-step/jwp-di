package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
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
            this.instantiateClass(beanType);
        }
    }

    Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return this.beans.entrySet().stream()
                .filter(bean -> bean.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object instantiateClass(Class<?> beanType) {
        if(beans.containsKey(beanType)) {
            return beans.get(beanType);
        }
        final Constructor<?> constructor = getConstructor(beanType);
        final Object instance = this.instantiateConstructor(constructor);
        beans.put(beanType, instance);
        return instance;
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final List<Object> args = Lists.newArrayListWithExpectedSize(parameterTypes.length);
        for(Class<?> clazz : parameterTypes) {
            final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
            final Object instance = this.instantiateClass(concreteClass);
            args.add(instance);
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
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
}
