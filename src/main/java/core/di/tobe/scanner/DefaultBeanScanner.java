package core.di.tobe.scanner;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactoryUtils;
import core.di.tobe.bean.BeanDefinition;
import core.di.tobe.BeanFactory;
import core.di.tobe.bean.DefaultBeanDefinition;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultBeanScanner implements BeanScanner {

    private final BeanFactory beanFactory;
    private final Reflections reflection;

    public DefaultBeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        this.reflection = new Reflections(basePackage);
    }

    public Set<BeanDefinition> enroll() {
        Set<Class<?>> annotatedWith = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);

        Set<BeanDefinition> beanDefinitions = annotatedWith.stream()
                .map(clazz -> BeanFactoryUtils.findConcreteClass(clazz, annotatedWith))
                .map(DefaultBeanDefinition::new)
                .collect(Collectors.toSet());

        beanFactory.registerBeans(beanDefinitions);
        return beanDefinitions;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflection.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}