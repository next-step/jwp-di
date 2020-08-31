package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public BeanFactory() {
        this.preInstanticateBeans = new HashSet<>();
    }

    public void applyConfiguration(Set<Class<?>> configurationBeans) {
        this.preInstanticateBeans.addAll(configurationBeans);
        initializeByConfig();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() throws BeanInitException {
        for (final Class<?> preInstanticateBean : preInstanticateBeans) {
            try {
                instantiateClass(preInstanticateBean);
            } catch (Exception e) {
                throw new BeanInitException(e.getMessage());
            }
        }

        logger.info("bean register start");
        for (final Class<?> aClass : beans.keySet()) {
            logger.info("bean register : {}", aClass);
        }
    }

    private void initializeByConfig() throws BeanInitException {
        for (final Class<?> preInstanticateBean : preInstanticateBeans) {
            final Set<Method> methods = BeanFactoryUtils.getBeanConstructor(preInstanticateBean);
            initializeByMethod(preInstanticateBean, methods);
        }

        logger.info("bean register start");
        for (final Class<?> aClass : beans.keySet()) {
            logger.info("bean register : {}", aClass);
        }
    }

    private void initializeByMethod(Class<?> preInstanticateBean, Set<Method> methods) {
        if (Objects.nonNull(methods)) {
            initializeByBean(preInstanticateBean, methods);
        }
    }

    private void initializeByBean(Class<?> preInstanticateBean, Set<Method> methods) {
        methods.forEach(method -> {
            try {
                instantiateBean(preInstanticateBean, method);
            } catch (Exception e) {
                throw new BeanInitException(e.getMessage());
            }
        });
    }


    private Object instantiateBean(Class<?> concreteClass, Method method) throws Exception {
        final Object bean = beans.get(method.getReturnType());
        if (Objects.nonNull(bean)) {
            return bean;
        }

        if (method.getParameterCount() == 0) {
            final Object invoke = method.invoke(concreteClass.newInstance());
            beans.put(method.getReturnType(), invoke);
            return invoke;
        }
        final Object invokeMethod = instantiateMethod(concreteClass, method);
        beans.put(method.getReturnType(), invokeMethod);
        return invokeMethod;
    }

    private Object instantiateMethod(Class clazz, Method method) throws Exception {
        List<Object> objects = Lists.newArrayList();
        for (final Class<?> parameterType : method.getParameterTypes()) {
            final Object bean = getBean(parameterType);
            if (Objects.nonNull(bean)) {
                objects.add(bean);
            } else {
                objects.add(instantiateBean(parameterType, method));
            }
        }
        return method.invoke(clazz.newInstance(), objects.toArray());
    }

    private Object instantiateClass(Class<?> clazz) throws Exception {
        final Object bean = beans.get(clazz);
        if (Objects.nonNull(bean)) {
            return bean;
        }

        final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        final Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);

        if (Objects.isNull(injectedConstructor)) {
            final Constructor<?> defaultConstructor = concreteClass.getConstructor();
            final Object constructor = defaultConstructor.newInstance();
            beans.put(concreteClass, constructor);
            return constructor;
        }

        final Object constructor = instantiateConstructor(injectedConstructor);
        beans.put(concreteClass, constructor);
        return constructor;
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws Exception {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> args = Lists.newArrayList();
        for (final Class<?> parameterType : parameterTypes) {
            final Object bean = getBean(parameterType);
            if (Objects.nonNull(bean)) {
                args.add(bean);
            } else {
                args.add(instantiateClass(parameterType));
            }

        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (final Class<?> clazz : preInstanticateBeans) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.put(clazz, beans.get(clazz));
            }
        }
        return controllers;
    }

    public Map<Class<?>, Object> getConfigurationBeans() {
        Map<Class<?>, Object> configurationBean = Maps.newHashMap();
        preInstanticateBeans.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Configuration.class))
                .forEach(clazz -> {
                    configurationBean.put(clazz, beans.get(clazz));
                    Arrays.stream(clazz.getMethods())
                            .filter(method -> method.isAnnotationPresent(Bean.class))
                            .forEach(method -> configurationBean.put(method.getReturnType(), beans.get(method.getReturnType())));
                });
        return configurationBean;
    }

}
