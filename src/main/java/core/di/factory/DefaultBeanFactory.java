package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import core.annotation.Qualifier;
import core.mvc.tobe.MethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<>();

    private Map<String, Object> beans = Maps.newHashMap();

    private BeanInitializer beanInitializer;

    public DefaultBeanFactory() {
        beanInitializer = new BeanInitializerComposite(new ClassBeanDefinitionInitializer());
    }

    public void initialize() {
        this.beanDefinitions.values().forEach(beanDefinition -> instantiateBeanDefinition(beanDefinition));
    }

    @Override
    public Object getBean(String name) {
        return beans.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return getBean(requiredType.getName(), requiredType);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        //        Inject 받아야할 Bean이 interface이고 Qualifier가 지정되지 않았을 시
        Object instance;
        if(requiredType.isInterface() && name.equals(requiredType.getName())) {
            instance = getImplBean(requiredType);
        } else if(name.equals(requiredType.getName())) {
            instance = doGetBean(name, requiredType);
        } else {
            instance = beans.get(name);
        }

        if(instance != null) {
            return (T) instance;
        }

        return instantiateBean(name, requiredType);
    }

    private <T> T instantiateBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = getBeanDefinition(name);

        if(beanDefinition == null) {
            throw new BeanInstantiationException(requiredType, "bean " + requiredType.getName() + " is not defined");
        }

        instantiateBeanDefinition(beanDefinition);
        return (T) getBean(beanDefinition.getName(), beanDefinition.getType());
    }

    private <T> T doGetBean(String name, Class<T> requiredType) {
        Object bean = beans.keySet().stream()
                .filter(key -> name.equals(key))
                .map(key -> beans.get(key))
                .findFirst()
                .orElse(null);

        if(bean != null) {
            return (T) bean;
        }

        Set<Object> typeBeans = beans.values().stream()
                .filter(instance -> requiredType.equals(instance.getClass()))
                .collect(Collectors.toSet());

        if(typeBeans.size() == 1) {
            return (T) typeBeans.iterator().next();
        }

        if(typeBeans.size() == 2) {
            throw new BeanInstantiationException(requiredType, requiredType.getName() + " need @Qualifier annotation to inject");
        }

        return null;
    }

    private Object getImplBean(Class<?> type) {
        Set<Object> instances = beans.values().stream()
                .filter(bean -> type.isAssignableFrom(bean.getClass()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if(instances.size() == 0) {
            BeanDefinition beanDefinition = getImplBeanDefinition(type);
            instantiateBeanDefinition(beanDefinition);
            return getBean(beanDefinition.getName(), beanDefinition.getType());
        }

        if(instances.size() >= 2) {
            throw new BeanInstantiationException(type, type.getName() + " need @Qualifier annotation to inject");
        }

        return instances.iterator().next();
    }

    private BeanDefinition getImplBeanDefinition(Class<?> type) {
        Set<BeanDefinition> implBeanDefinitions = beanDefinitions.values().stream()
                .filter(beanDefinition -> type.isAssignableFrom(beanDefinition.getType()))
                .collect(Collectors.toSet());

        if(implBeanDefinitions.size() >= 2) {
            throw new BeanInstantiationException(type, type.getName() + " need @Qualifier annotation to inject");
        }

        if(implBeanDefinitions.size() < 1) {
            throw new BeanInstantiationException(type, "there is no implementation of " + type.getName());
        }

        return implBeanDefinitions.iterator().next();
    }

    @Override
    public Object[] getAnnotatedBeans(Class<? extends Annotation> annotation) {
        return beans.values().stream()
                .filter(obj -> obj.getClass().isAnnotationPresent(annotation))
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .toArray(new Object[] {});
    }

    private void instantiateBeanDefinition(BeanDefinition beanDefinition) {
        Object instantiate = beanInitializer.instantiate(beanDefinition, this);
        beans.put(beanDefinition.getName(), instantiate);
    }

    @Override
    public void registerDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.put(beanDefinition.getName(), beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> type) {
        return this.beanDefinitions.get(type.getName());
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }
}
