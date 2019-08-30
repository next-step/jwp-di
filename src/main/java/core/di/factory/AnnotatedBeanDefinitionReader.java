package core.di.factory;

import core.annotation.Bean;
import core.di.factory.config.AnnontatedBeanDefinition;
import core.di.factory.support.BeanDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class AnnotatedBeanDefinitionReader {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedBeanDefinitionReader.class);
    private BeanDefinitionRegistry registry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void register(Class<?>[] configClass) {
        for (Class clazz : configClass) {
            roadBeanMethod(clazz);
        }
    }

    private void roadBeanMethod(Class clazz) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            registerBeanDefinition(clazz, method);
        }
    }

    private void registerBeanDefinition(Class clazz, Method method) {
        if (method.isAnnotationPresent(Bean.class)) {
            registry.registerBeanDefinition(new AnnontatedBeanDefinition(clazz, method));
        }
    }
}
