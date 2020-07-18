package core.di.factory;

import com.google.common.collect.Maps;
import core.BeanMethodInfo;
import core.annotation.Bean;
import core.annotation.ComponentScan;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created By kjs4395 on 7/17/20
 */
public class ConfigurationFactory {
    /**
     * 1. configuration 어노테이션이 붙은 class 찾기
     * 2. 그 중에 bean 어노테이션이 붙은 class 찾기
     */

    private Set<Class<?>> configurations;

    private Map<Class<?>, BeanMethodInfo> beanMethodInfos = Maps.newHashMap();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public ConfigurationFactory(Set<Class<?>> configurations) {
        this.configurations = configurations;
        for(Class<?> configuration : configurations) {
            beanMethodInfos.putAll(findBeanMethod(configuration));
        }
    }

    public void initialize() {
        for(Class<?> findBean :beanMethodInfos.keySet()) {
            getBean(findBean);
        }
    }

    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }
        return (T) makeBean(requiredType);
    }

    private Object makeBean(Class<?> findClass) {
        BeanMethodInfo beanMethodInfo = beanMethodInfos.get(findClass);

        if(beanMethodInfo.hasNoParameter()) {
            Object bean = beanMethodInfo.methodInvoke(new Object[0]);
            beans.put(beanMethodInfo.getReturnType(), bean);
            return bean;
        }

        return injectBean(findClass);
    }

    private <T> T injectBean(Class<T> injectClass) {
        //method invoke 하구
        BeanMethodInfo beanMethodInfo = beanMethodInfos.get(injectClass);
        Object[] parameterValues = new Object[beanMethodInfo.getParameters().length];

        for(int i=0; i<beanMethodInfo.getParameters().length; i++) {
            parameterValues[i] = getBean(beanMethodInfo.getParameters()[i].getType());
        }
        Object bean = beanMethodInfo.methodInvoke(parameterValues);
        beans.put(beanMethodInfo.getReturnType(),bean);
        return (T) bean;
    }


    private Map<Class<?>, BeanMethodInfo> findBeanMethod(Class<?> configClass) {
        return Arrays.stream(configClass.getMethods())
                .filter(method -> method.getAnnotation(Bean.class) != null)
                .map(method -> {return new BeanMethodInfo(method, BeanUtils.instantiateClass(configClass),method.getReturnType());})
                .collect(Collectors.toMap(beanMethodInfo -> beanMethodInfo.getReturnType(), Function.identity()));
    }

    public String[] getComponentScan() {
        Class<?> componentClass = configurations.stream()
                .filter(clazz-> clazz.isAnnotationPresent(ComponentScan.class))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        ComponentScan componentScan = componentClass.getAnnotation(ComponentScan.class);
        return componentScan.value();
    }

    public Map<Class<?>,Object> getAllBeans() {
        return this.beans;
    }

}
