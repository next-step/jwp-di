package core.di;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz){
        if(!clazz.isAnnotationPresent(Configuration.class)){
            return;
        }

        Set<BeanDefinition> beanDefinitions = createBeanDefinitions(clazz);
        this.beanFactory.addBeanDefinitions(beanDefinitions);
    }

    private Set<BeanDefinition> createBeanDefinitions(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        return Stream.of(methods).filter(method -> method.isAnnotationPresent(Bean.class))
            .map(method -> new BeanDefinition() {
                @Override
                public String getName() {
                    return method.getName();
                }

                @Override
                public Method getMethod() {
                    return method;
                }

                @Override
                public Constructor getConstructor() {
                    return null;
                }

                @Override
                public Class<?> getBeanClass() {
                    return method.getReturnType();
                }
            })
            .collect(Collectors.toSet());
    }


}
