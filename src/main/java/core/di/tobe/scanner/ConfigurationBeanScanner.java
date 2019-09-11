package core.di.tobe.scanner;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.tobe.bean.BeanDefinition;
import core.di.tobe.BeanFactory;
import core.di.tobe.bean.ConfigurationBeanDefinition;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class ConfigurationBeanScanner implements BeanScanner {

    private final BeanFactory beanFactory;
    private final Reflections reflection;

    public ConfigurationBeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        this.reflection = new Reflections(basePackage);
    }

    public Set<BeanDefinition> enroll() {
        Set<BeanDefinition> beanDefinitions = Sets.newHashSet();
        Set<Class<?>> annotatedWith = getTypesAnnotatedWith(Configuration.class);
        for (Class<?> clazz : annotatedWith) {
            Set<Method> methods = ReflectionUtils.getMethods(clazz, ReflectionUtils.withAnnotation(Bean.class));
            for (Method method : methods) {
                beanDefinitions.add(new ConfigurationBeanDefinition(clazz, method));
            }
        }

        beanFactory.registerBeans(beanDefinitions);
        return beanDefinitions;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflection.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
