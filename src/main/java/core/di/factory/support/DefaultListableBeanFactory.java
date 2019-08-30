package core.di.factory.support;

import com.google.common.collect.Maps;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.config.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultListableBeanFactory implements BeanFactory, BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

    private Set<Class<?>> classPathBeans;
    private Set<BeanDefinition> beanDefinitionMap = new HashSet<>();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public DefaultListableBeanFactory() {
    }

    public DefaultListableBeanFactory(Set<Class<?>> classPathBeans) {
        this.classPathBeans = classPathBeans;
        initialize();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> clazz : classPathBeans) {
            initBeans(clazz);
        }
    }

    private void initBeans(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        beans.put(clazz, getInstance(clazz, injectedConstructor));
    }

    private Object getInstance(Class<?> clazz, Constructor<?> injectedConstructor) {
        return injectedConstructor == null ? BeanUtils.instantiateClass(clazz) : getInjectInstance(injectedConstructor);
    }

    private Object getInjectInstance(Constructor<?> injectedConstructor) {
        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        List params = makeConstructorParams(parameterTypes);
        return BeanUtils.instantiateClass(injectedConstructor, params.stream().toArray());
    }

    private List makeConstructorParams(Class<?>[] parameterTypes) {
        List params = new ArrayList();
        for (Class clazz : parameterTypes) {
            params.add(getConcreteClass(clazz));
        }
        return params;
    }

    private Object getConcreteClass(Class clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, classPathBeans);
        if (beans.get(concreteClass) == null) {
            initBeans(concreteClass);
        }
        return beans.get(concreteClass);
    }

    @Override
    public Map<Class<?>, Object> getAnnotationTypeClass(Class<? extends Annotation> annotation) {
        return beans.keySet().stream()
                .filter(key -> key.isAnnotationPresent(annotation))
                .collect(Collectors.toMap(key -> key, key -> beans.get(key)));
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        beanDefinitionMap.add(beanDefinition);
    }

    public void instantiateBeans() {


    }
}
