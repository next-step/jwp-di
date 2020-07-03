package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.util.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author KingCjy
 */
public class MethodBeanScanner implements BeanScanner {

    public final BeanDefinitionRegistry beanDefinitionRegistry;

    public MethodBeanScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void scan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> configurationClasses = ReflectionUtils.getAnnotatedClasses(reflections, Configuration.class);

        Set<MethodBeanDefinition> beanDefinitions = createMethodBeanDefinitions(configurationClasses);

        for (MethodBeanDefinition beanDefinition : beanDefinitions) {
            beanDefinitionRegistry.registerDefinition(beanDefinition);
        }
    }

    private Set<MethodBeanDefinition> createMethodBeanDefinitions(Set<Class<?>> configurationClasses) {
        Set<MethodBeanDefinition> methods = new LinkedHashSet<>();
        for (Class<?> targetClass : configurationClasses) {
            for (Method method : targetClass.getMethods()) {
                if(method.isAnnotationPresent(Bean.class)) {
                    methods.add(new MethodBeanDefinition(method));
                }
            }
        }

        return methods;
    }
}
