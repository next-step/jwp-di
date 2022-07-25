package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Set;

public class  ClasspathBeanDefinitionScanner implements BeanDefinitionScanner {

    private final BeanFactory beanFactory;
    private final Object[] basePackages;

    public ClasspathBeanDefinitionScanner(BeanFactory beanFactory, Object[] basePackages) {
        this.beanFactory = beanFactory;
        this.basePackages = basePackages;
    }

    @Override
    public void scan() {
        Reflections reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> beans = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        beans.forEach(it -> beanFactory.registerBeanDefinition(it, new ClasspathBeanDefinition(it)));
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
