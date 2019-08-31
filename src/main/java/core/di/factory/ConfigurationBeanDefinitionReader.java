package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.di.factory.config.AnnotatedBeanDefinition;
import core.di.factory.config.BeanDefinition;
import core.di.factory.config.ClassBeanDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanDefinitionReader implements BeanDefinitionLoader<Class<?>>{

    private final BeanFactory beanFactory;

    public ConfigurationBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void loadBeanDefinitions(Class<?>... annotatedClasses){
        for(Class<?> clazz : annotatedClasses) {
            Set<BeanDefinition> beanDefinitions = getBeanDefinitions(clazz);
            beanFactory.addBeandDefinitions(beanDefinitions);
        }
    }

    public Set<BeanDefinition> getBeanDefinitions(Class<?>... annotatedClasses){
        return Arrays.stream(annotatedClasses)
                .map(this::getBeanDefinitions)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<BeanDefinition> getBeanDefinitions(Class<?> annotatedClass){
        Set<BeanDefinition> beanDefinitions = Sets.newHashSet();
        beanDefinitions.add(new ClassBeanDefinition(annotatedClass));

        Set<Method> annotatedBeanMethods = BeanFactoryUtils.getAnnotatedMethods(annotatedClass, Bean.class);
        for(Method method : annotatedBeanMethods) {
            beanDefinitions.add(new AnnotatedBeanDefinition(annotatedClass, method));
        }
        return beanDefinitions;
    }
}
