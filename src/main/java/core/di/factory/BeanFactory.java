package core.di.factory;

import com.google.common.collect.Maps;
import core.util.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) this.beans.get(requiredType);
    }

    public void initialize() {
        for (Class clazz : this.preInstanticateBeans) {
            Object bean = getOrNewBean(clazz);
            this.beans.put(clazz, bean);
        }
    }

    public Map<Class<?>, Object> getAnnotationBeans(Class<?extends Annotation> annotation) {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : preInstanticateBeans) {
            if (clazz.isAnnotationPresent(annotation)) {
                controllers.put(clazz, beans.get(clazz));
            }
        }
        return controllers;
    }

    private Object getOrNewBean(Class clazz){
        if(this.beans.containsKey(clazz)){
            return this.beans.get(clazz);
        }

        return newInstance(clazz);
    }

    private Object newInstance(Class clazz) {
        Class concreteClass = BeanFactoryUtils.findConcreteClass(clazz, this.preInstanticateBeans);
        Constructor constructor = findConstructor(concreteClass);
        Object[] injectBeans = getInjectBeans(constructor);
        Object bean = BeanUtils.instantiateClass(constructor, injectBeans);
        return bean;
    }

    private Constructor findConstructor(Class clazz) {
        return BeanFactoryUtils.getInjectedConstructor(clazz)
            .orElseGet(() -> ReflectionUtils.getConstructorByArgs(clazz));
    }

    private Object[] getInjectBeans(Constructor constructor) {
        return Arrays.stream(constructor.getParameterTypes())
            .map(parameter -> newInstance(parameter))
            .toArray();
    }

}
