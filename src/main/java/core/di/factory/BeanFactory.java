package core.di.factory;

import com.google.common.collect.Maps;
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
    private final Set<Class<?>> preInstanticateBeans;

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
            return createOrPutNewBeanByDefaultConstructor(preInstanticateBean);
        }

        return createOrPutNewBean(preInstanticateBean, injectedConstructor);
    }

    private Object createOrPutNewBeanByDefaultConstructor(Class<?> preInstanticateBean) {
        Object result = BeanUtils.instantiateClass(preInstanticateBean);
        beans.put(preInstanticateBean, result);
        return result;
    }

    private Object createOrPutNewBean(Class<?> preInstanticateBean, Constructor<?> injectedConstructor) {
        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        List<Object> parameters = instantiateDependencyArguments(parameterTypes);

        Object object = BeanUtils.instantiateClass(injectedConstructor, parameters.toArray());
        beans.put(preInstanticateBean, object);
        return object;
    }

    private List<Object> instantiateDependencyArguments(Class<?>[] parameterTypes) {
        List<Object> parameters = new ArrayList<>();

        for (Class<?> parameterType : parameterTypes) {
            Class<?> concreteType = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
            parameters.add(instantiateBean(concreteType));
        }
        return parameters;
    }
}
