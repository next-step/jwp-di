package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public Set<Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(annotationType))
                .collect(Collectors.toSet());
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
        List<Object> parameters = new ArrayList<>();

        for (Class<?> parameterType : parameterTypes) {
            Class<?> concreteType = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
            parameters.add(instantiateBean(concreteType));
        }

        Object object = BeanUtils.instantiateClass(injectedConstructor, parameters.toArray());
        beans.put(preInstanticateBean, object);
        return object;
    }
}
