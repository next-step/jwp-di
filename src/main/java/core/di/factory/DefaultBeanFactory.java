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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<>();
    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private Map<String, Object> beans = Maps.newHashMap();

    @Override
    public Object getBean(String name) {
        return beans.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType.getName());
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return (T) beans.get(name);
    }

    @Override
    public Object[] getAnnotatedBeans(Class<? extends Annotation> annotation) {
        return beans.values().stream()
                .filter(obj -> obj.getClass().isAnnotationPresent(annotation))
                .collect(Collectors.toSet())
                .toArray(new Object[] {});
    }


    public void initialize() {
        this.beanDefinitions.values().forEach(beanDefinition -> instantiateBeanDefinition(beanDefinition));
    }

    private void instantiateBeanDefinition(BeanDefinition beanDefinition) {
        if(getBean(beanDefinition.getName(), beanDefinition.getType()) != null) {
            return;
        }

        Constructor constructor = findInjectController(beanDefinition.getType());

        if(constructor == null) {
            throw new BeanInstantiationException(beanDefinition.getType(), "Constructor with @Inject not Found");
        }

        MethodParameter[] methodParameters = getMethodParameters(constructor);
        Object[] parameters = getParameters(methodParameters);

        Object instance = BeanUtils.instantiateClass(constructor, parameters);
        logger.info("bean " + instance.getClass() + " instantiate");
        beans.put(beanDefinition.getName(), instance);
    }

    private Object[] getParameters(MethodParameter[] methodParameters) {
        Object[] parameters = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            String beanName = getBeanName(methodParameters[i]);
            parameters[i] = getInstanceBean(methodParameters[i].getType(), beanName);
        }

        return parameters;
    }

    private String getBeanName(MethodParameter methodParameter) {
        String name = methodParameter.getType().getName();
        Qualifier qualifier = methodParameter.getAnnotation(Qualifier.class);

        return qualifier == null ? name : qualifier.value();
    }

    private MethodParameter[] getMethodParameters(Constructor constructor) {
        MethodParameter[] methodParameters = new MethodParameter[constructor.getParameters().length];
        String[] parameterNames = nameDiscoverer.getParameterNames(constructor);
        Parameter[] parameters = constructor.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = new MethodParameter(constructor, parameters[i].getType(), parameters[i].getAnnotations(), parameterNames[i]);
        }

        return methodParameters;
    }

    private Object getInstanceBean(Class<?> type, String beanName) {
//        Inject 받아야할 Bean이 interface이고 Qualifier가 지정되지 않았을 시
        if(type.isInterface() && beanName.equals(type.getName())) {
            BeanDefinition implBeanDefinition = getImplBeanDefinition(type);
            instantiateBeanDefinition(implBeanDefinition);
            return beans.get(implBeanDefinition.getName());
        }

        if(beans.get(beanName) == null) {
            instantiateBeanDefinition(getBeanDefinition(beanName));
        }

        return beans.get(beanName);
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

    private Constructor findInjectController(Class<?> targetClass) {
        for (Constructor<?> constructor : targetClass.getConstructors()) {
            if(constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }

        try {
            return targetClass.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
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
