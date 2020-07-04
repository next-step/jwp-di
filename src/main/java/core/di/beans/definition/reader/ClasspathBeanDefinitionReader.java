package core.di.beans.definition.reader;

import core.di.beans.definition.BeanDefinitionRegistry;
import core.di.beans.definition.DefaultBeanDefinition;
import core.util.ReflectionUtils;
import org.reflections.Reflections;

import java.util.Set;

import static core.di.factory.BeanFactory.TARGET_ANNOTATION_TYPES;

public class ClasspathBeanDefinitionReader {
    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ClasspathBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @SuppressWarnings("unchecked")
    public void doScan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> beanTypes = ReflectionUtils.getTypesAnnotatedWith(reflections, TARGET_ANNOTATION_TYPES);
        for (Class<?> beanType : beanTypes) {
            beanDefinitionRegistry.register(beanType, new DefaultBeanDefinition(beanType));
        }
    }
}
