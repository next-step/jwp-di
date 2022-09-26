package core.di;

import core.annotation.Bean;
import org.reflections.ReflectionUtils;
import org.reflections.util.ReflectionUtilsPredicates;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotatedBeanDefinitionScanner implements BeanScanner {
    private final BeanDefinitionRegistry registry;

    public AnnotatedBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.registry = beanDefinitionRegistry;
    }

    @Override
    public void scan(Set<Class<?>> configurationClasses) {
        for (Class<?> configurationClass : configurationClasses) {
            Set<Method> beanMethods = ReflectionUtils.getAllMethods(configurationClass, ReflectionUtilsPredicates.withAnnotation(Bean.class));
            this.registry.registerConfigurationBeans(getMethodBeanDefinitions(configurationClass, beanMethods));
        }
    }

    private Set<BeanDefinition> getMethodBeanDefinitions(Class<?> configurationClass, Set<Method> beanMethods) {
        return beanMethods.stream()
                .map(method -> new MethodBeanDefinition(configurationClass, method))
                .collect(Collectors.toSet());
    }
}
