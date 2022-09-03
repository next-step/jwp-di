package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.config.WebMvcConfiguration;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public class ClasspathBeanScanner implements BeanScanner {
    private Reflections reflections;

    @Override
    public Set<BeanRegister> scan(Class<? extends WebMvcConfiguration> configurationClazz) {
        Set<BeanRegister> scanResults = Sets.newHashSet();
        if (!configurationClazz.isAnnotationPresent(ComponentScan.class)) {
            return scanResults;
        }

        ComponentScan annotation = configurationClazz.getAnnotation(ComponentScan.class);
        for (String path : annotation.basePackages()) {
            scanResults.addAll(scan(path));
        }

        return scanResults;
    }

    @SuppressWarnings("unchecked")
    public Set<BeanRegister> scan(String path) {
        reflections = new Reflections(path);

        return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class).stream()
                .map(ClassBeanRegister::new)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
