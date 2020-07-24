package core.di.config;

import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class AnnotationConfigurationMapping {

    private Set<Class<?>> typesAnnotatedWith;
    private BeanFactory beanFactory;

    public AnnotationConfigurationMapping() {
        initiate();
    }

    private void initiate() {
        Reflections reflections = new Reflections("");
        typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
    }

    public void scan() {
        beanFactory = new BeanFactory(typesAnnotatedWith);
        beanFactory.initialize();
        beanFactory.initializeByConfig();
    }

    public Object getBean(Class clazz) {
        return beanFactory.getBean(clazz);
    }

    public Set<Class<?>> getTypesAnnotatedWith() {
        return new HashSet<>(typesAnnotatedWith);
    }
}
