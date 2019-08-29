package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
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

    public void initialize() {
        for(Class<?> clazz : this.preInstanticateBeans) {
            beans.put(clazz, getInstantiateClass(clazz));
        }
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass){
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object getInstantiateClass(Class<?> clazz) {
        return beans.computeIfAbsent(clazz, this::instantiate);
    }

    private Object instantiate(Class<?> clazz) {
        Constructor<?> constructor = getConstructor(clazz);
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for(int i = 0 ; i < parameterTypes.length; i ++) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterTypes[i], this.preInstanticateBeans);
            args[i] = instantiate(concreteClass);
        }

        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("인스턴스화 중 문제 발생", e);
        }
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if(constructor != null) {
            return constructor;
        }

        return BeanFactoryUtils.getNoArgsConstructor(clazz);
    }


}
