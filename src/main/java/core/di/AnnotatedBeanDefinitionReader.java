package core.di;

import static org.reflections.util.ReflectionUtilsPredicates.*;

import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.ReflectionUtils;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;

public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void register(Class<?>... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerBean(Class<?> componentClass) {
        if (!componentClass.isAnnotationPresent(Configuration.class)) {
            return;
        }

        registry.registerBeanDefinition(componentClass, new BeanDefinition(componentClass));

        Set<Method> beanMethods = ReflectionUtils.getAllMethods(componentClass, withAnnotation(Bean.class));
        for (Method beanMethod : beanMethods) {
            BeanDefinition beanDefinition = new BeanDefinition(componentClass, beanMethod);
            registry.registerBeanDefinition(beanMethod.getReturnType(), beanDefinition);
        }

        if (componentClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = componentClass.getAnnotation(ComponentScan.class);
            Object[] packages = annotation.value();
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
            scanner.scan(packages);
        }
    }
}
