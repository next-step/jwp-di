package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.di.bean.BeanDefinition;
import core.di.bean.MethodBeanDefinition;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by hspark on 2019-09-01.
 */
public class ConfigurationBeanScanner implements BeanScanner {

    private Set<Class<?>> configurationClasses = Sets.newHashSet();
    private BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory BeanFactory) {
        this.beanFactory = BeanFactory;
    }

    @Override
    public void scan() {
        Object[] basePackages = getBasePackages(configurationClasses);
        if (ArrayUtils.isNotEmpty(basePackages)) {
            BeanScanner annotatedBeanScanner = new AnnotatedBeanScanner(beanFactory, basePackages);
            annotatedBeanScanner.scan();
        }
        for (Class<?> aClass : configurationClasses) {
            List<Method> beanMethods = Arrays.stream(aClass.getDeclaredMethods())
                    .filter(method -> Objects.nonNull(method.getAnnotation(Bean.class)))
                    .collect(Collectors.toList());
            Object configurationObject = BeanUtils.instantiateClass(aClass);
            registerBeanMethod(configurationObject, beanMethods);
        }
    }

    public void registerConfiguration(Class<?>... configurationClasses) {
        this.configurationClasses.addAll(Sets.newHashSet(configurationClasses));
    }

    public void registerConfiguration(Set<Class<?>> configurationClasses) {
        this.configurationClasses.addAll(configurationClasses);
    }

    private void registerBeanMethod(Object configurationObject, List<Method> beanMethods) {
        for (Method beanMethod : beanMethods) {
            BeanDefinition beanDefinition = new MethodBeanDefinition(configurationObject, beanMethod);
            beanFactory.registerBeanDefinition(beanDefinition);
        }
    }

    private Object[] getBasePackages(Set<Class<?>> configurationClass) {
        return configurationClass.stream()
                .filter(it -> Objects.nonNull(it.getAnnotation(ComponentScan.class)))
                .flatMap(it -> Arrays.stream(it.getAnnotation(ComponentScan.class).value()))
                .collect(Collectors.toList()).toArray();
    }
}
