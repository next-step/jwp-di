package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClasspathBeanScanner {
    private final BeanFactory beanFactory;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public void doScan(String prefix) {
        Reflections reflections = new Reflections(prefix, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class)
                .forEach(clazz -> beans.put(clazz, instanticateBean(clazz)));
        beanFactory.addBean(beans);
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    private Object instanticateBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }
        return instanticate(clazz);
    }

    private Object instanticate(Class<?> clazz) {
        final Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (constructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }
        return BeanUtils.instantiateClass(constructor, getArgs(constructor));
    }

    private Object[] getArgs(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final List<Object> args = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, beans.keySet());
            final Object instance = getInstance(concreteClass);
            args.add(instance);
            beans.put(concreteClass, instance);
        }
        return args.toArray();
    }

    private Object getInstance(Class<?> concreteClass) {
        if (beans.containsKey(concreteClass)) {
            return beans.get(concreteClass);
        }
        return instanticateBean(concreteClass);
    }
}
