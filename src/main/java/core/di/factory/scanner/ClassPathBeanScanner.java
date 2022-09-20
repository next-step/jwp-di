package core.di.factory.scanner;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.definition.BeanDefinition;
import core.di.factory.definition.BeanDefinitionRegistry;
import core.di.factory.definition.ClassPathBeanDefinition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class ClassPathBeanScanner implements BeanScanner {
    private Reflections reflections;
    private BeanDefinitionRegistry beanDefinitionRegistry;

    public ClassPathBeanScanner(BeanDefinitionRegistry beanDefinitions) {
        this.beanDefinitionRegistry = beanDefinitions;
    }

    @Override
    public void doScan(Class<?>... configurations) {
        beanDefinitionRegistry.addAllPreInstantiateBeans(scan(getBasePackages(configurations)));
    }

    private Set<Class<?>> scan(Object... basePackage) {
        reflections = new Reflections(basePackage);
        return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    private Object[] getBasePackages(Class<?>[] configurations) {
        return Arrays.stream(configurations)
                .filter(configuration -> configuration.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .flatMap(Stream::of)
                .toArray();
    }
}
