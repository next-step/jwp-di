package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, BeanDefinition> beanDefs = Maps.newHashMap();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {}

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> clazz : beanDefs.keySet()) {
            if (getBean(clazz) != null) {
                continue;
            }
            addBean(clazz, getinstanicatedClass(clazz));
        }
    }

    private Object getinstanicatedClass(Class<?> clazz) {
        BeanDefinition beanDefinition = this.beanDefs.get(clazz);

        if (beanDefinition == null) {
            beanDefinition = this.beanDefs.get(findConcreteClass(clazz, beanDefs.keySet()));
        }

        if (beanDefinition == null) {
            throw new RuntimeException(String.format("정의된 Bean Type[%s]이 없습니다.", clazz.getName()));
        }
        return initBeanDef(beanDefinition);
    }

    private Object initBeanDef(BeanDefinition beanDef) {
        Object bean = null;
        if (beanDef instanceof ComponentBeanDefinition) {
            bean = instanticateClass(beanDef.getBeanType());
        }

        if (beanDef instanceof AnnotationBeanDefinition) {
            bean = instanticateMethod((AnnotationBeanDefinition) beanDef);
        }
        return bean;
    }

    private Object instanticateMethod(AnnotationBeanDefinition beanDef) {
        List<Object> parameters = getParameters(beanDef.getParameters());
        try {
            return beanDef.getMethod().invoke(beanDef.getInstanceClass(), parameters.toArray());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.error("Bean 생성 실패 ", e);
            throw new RuntimeException("Bean 생성 실패 ", e);
        }
    }

    private Object instanticateClass(Class clazz) {
        Constructor constructor = getInjectedConstructor(clazz);
        if (constructor != null) {
            return instanticateConstructor(getInjectedConstructor(findConcreteClass(clazz, beanDefs.keySet())));
        }

        return BeanUtils.instantiateClass(findConcreteClass(clazz, beanDefs.keySet()));
    }

    private Object instanticateConstructor(Constructor constructor) {
        List<Object> parameters = getParameters(constructor.getParameterTypes());
        return BeanUtils.instantiateClass(constructor, parameters.toArray());
    }

    private List<Object> getParameters(Class[] parameterTypes) {
        List<Object> parameters = new ArrayList<>();
        for (Class parameterType : parameterTypes) {
            Object bean = getinstanicatedClass(parameterType);
            parameters.add(bean);
        }
        return parameters;
    }

    private void addBean(Class<?> key, Object value) {
        beans.put(key, value);
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (BeanDefinition beanDef : beanDefs.values()) {
            if (beanDef.isAnnotation(Controller.class)) {
                controllers.put(beanDef.getBeanType(), beans.get(beanDef.getBeanType()));
            }
        }

        return controllers;
    }

    public void addBeanDefs(Set<BeanDefinition> beanDefs) {
        for (BeanDefinition beanDef : beanDefs) {
            addBeanDef(beanDef);
        }
    }

    private void addBeanDef(BeanDefinition beanDef) {
        if (beanDefs.containsKey(beanDef.getBeanType())) {
            return;
        }
        beanDefs.put(beanDef.getBeanType(), beanDef);
    }
}
