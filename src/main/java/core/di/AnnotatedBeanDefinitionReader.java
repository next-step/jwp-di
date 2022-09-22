package core.di;

import java.util.Set;

public class AnnotatedBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.registry = beanDefinitionRegistry;
    }

    public void register(Set<Class<?>> configurationClasses) {
        this.registry.registerConfigurationBeans(configurationClasses);
    }
}
