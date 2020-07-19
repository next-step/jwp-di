package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private Set<Class<?>> preInstantiateBeans;

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MemberUsageScanner(), new MethodAnnotationsScanner(), new MethodParameterScanner()));
        Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);

        try {
            for (Class<?> configurationClass : configurationClasses) {
                Object instance = configurationClass.newInstance();
                Method[] methods = configurationClass.getDeclaredMethods();
                Arrays.stream(methods)
                        .filter(m -> m.getDeclaredAnnotation(Bean.class) != null)
                        .forEach(m -> addConfigurationBean(m, instance));
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        beans.putAll(preInstantiateBeans());
    }

    public Map<Class<?>, Object> preInstantiateBeans() {
        return preInstantiateBeans.stream()
                .collect(Collectors.toMap(b -> b, this::instantiateClass));
    }

    public Object instantiateClass(Class<?> clazz) {
        try {
            if (beans.containsKey(clazz)) {
                return beans.get(clazz);
            }
            Class<?> conClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
            Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (constructor == null) return conClass.newInstance();
            logger.debug("{}", constructor);
            return instantiateConstructor(constructor);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> args = new ArrayList<>();
        for (Class<?> clazz : parameterTypes) {
            Object bean = instantiateClass(clazz);
            args.add(bean);
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : preInstantiateBeans) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.put(clazz, beans.get(clazz));
            }
        }
        return controllers;
    }

    private void addConfigurationBean(Method method, Object instance) {
        try {
            Class<?> returnType = method.getReturnType();
            Object obj = method.invoke(instance);

            beans.put(returnType, obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
