package core.di.factory;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.config.BeanDefinition;
import core.di.factory.config.ClassBeanDefinition;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathBeanDefinitionScanner implements BeanDefinitionLoader<Object>{

    private final List<Class<? extends Annotation>> TARGET_ANNOTATIONS = Arrays.asList(Controller.class
            , Service.class
            , Component.class
            , Repository.class);

    private final BeanFactory beanFactory;

    public ClassPathBeanDefinitionScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void loadBeanDefinitions(Object... basePackage){
        for (Class<?> clazz : this.getPreInstanticateClasses(basePackage)) {
            this.beanFactory.addBeandDefinition(new ClassBeanDefinition(clazz));
        }
    }

    public Set<BeanDefinition> getBeanDefinitions(Object... basePackage){
        return this.getPreInstanticateClasses(basePackage).stream()
                .map(ClassBeanDefinition::new)
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getPreInstanticateClasses(Object[] basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return TARGET_ANNOTATIONS.stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }


}
