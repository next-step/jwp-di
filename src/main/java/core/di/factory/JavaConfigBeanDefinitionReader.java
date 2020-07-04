package core.di.factory;

import core.annotation.Bean;

import java.util.Arrays;

public class JavaConfigBeanDefinitionReader implements BeanDefinitionReader {

    final BeanFactory beanFactory;

    public JavaConfigBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void loadBeanDefinitions(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            registerBeanDefinition(clazz);
        }
    }

    private void registerBeanDefinition(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> {
                    final BeanDefinition beanDefinition = new JavaConfigBeanDefinition(method);
                    beanFactory.registerBeanDefinition(beanDefinition.getOriginalClass(), beanDefinition);
                });

    }
}
