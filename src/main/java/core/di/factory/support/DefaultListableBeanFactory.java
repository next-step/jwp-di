package core.di.factory.support;

import com.google.common.collect.Maps;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.config.AnnontatedBeanDefinition;
import core.di.factory.config.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultListableBeanFactory implements BeanFactory, BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

    private Set<BeanDefinition> beanDefinitionMap = new HashSet<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public DefaultListableBeanFactory() {
    }

    public void instantiateBeans() {
        for (BeanDefinition definition : beanDefinitionMap) {
            instantiateDefinition(definition);
        }
    }

    private void instantiateDefinition(BeanDefinition definition) {
        if (definition.isAnnotatedDefinition()) {
            initAnnotatedDefinition(definition);
            return;
        }
        initBeans(definition.getBeanClass());
    }

    private void initAnnotatedDefinition(BeanDefinition beanDefinition) {
        AnnontatedBeanDefinition annontatedBeanDefinition = (AnnontatedBeanDefinition) beanDefinition;
        Method method = annontatedBeanDefinition.getMethod();
        try {
            beans.put(method.getReturnType(), getInstance(method));
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("error : {}", e.getMessage());
        }
    }

    private Object getInstance(Method method) throws InvocationTargetException, IllegalAccessException {
        if (method.getParameterCount() == 0) {
            return method.invoke(BeanUtils.instantiateClass(method.getDeclaringClass()));
        }
        return getInjectInstance(method);
    }

    private Object getInjectInstance(Method method) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List params = makeConstructorParams(parameterTypes);
        return method.invoke(BeanUtils.instantiateClass(method.getDeclaringClass()), params.stream().toArray());
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
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
        if (beans.get(clazz) == null) {
            instantiateBeans(clazz);
        }
        return beans.get(clazz);
    }

    public void instantiateBeans(Class<?> clazz) {
        BeanDefinition definition = getDefinitionByClass(clazz);
        instantiateDefinition(definition);
    }

    private BeanDefinition getDefinitionByClass(Class<?> clazz) {
        return beanDefinitionMap.stream()
                .filter(beanDefinition -> beanDefinition.getBeanClass().getName().equals(clazz.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("no definition class"));
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

    @Override
    public Set<BeanDefinition> getDefinitions() {
        return Collections.unmodifiableSet(beanDefinitionMap);
    }

}
