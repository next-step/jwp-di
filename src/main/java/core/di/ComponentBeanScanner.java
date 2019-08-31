package core.di;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ComponentBeanScanner implements BeanScanner {

    private Reflections reflections;
    private BeanFactory beanFactory;

    public ComponentBeanScanner(BeanFactory beanFactory, Collection<String> basePackage) {
        reflections = new Reflections(basePackage);
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void scan() {

        Set<BeanDefinition> definitions = Sets.newHashSet();
        Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);

        for (Class<?> instantiateClazz : preInstantiateClazz) {
            definitions.add(new BeanDefinition(instantiateClazz));
        }

        beanFactory.registerBeanDefinition(definitions);
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
