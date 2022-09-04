package next.config;

import java.util.Arrays;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.BeanDefinition;
import core.di.factory.BeanFactory;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> componentClass) {
        if (!componentClass.isAnnotationPresent(Configuration.class)) {
            return;
        }

        beanFactory.registerBeanDefinition(componentClass, new BeanDefinition(componentClass));

        Arrays.stream(componentClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Bean.class))
            .forEach(method -> beanFactory.registerBeanDefinition(method.getReturnType(), new BeanDefinition(componentClass, method)));
    }
}
