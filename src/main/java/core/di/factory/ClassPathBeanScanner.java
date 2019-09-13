package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

public class ClassPathBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClassPathBeanScanner.class);

    private Reflections reflections;

    private PreInstanceBeanHandler pibh;

    public ClassPathBeanScanner(PreInstanceBeanHandler pibh) {
        this.pibh = pibh;
    }

    public void doScan(Object... basePackage) {
        reflections = new Reflections(basePackage);
        pibh.registerPreInstantiateBeans(getPreInstantiateBeans());
    }

    public Set<Class<?>> getPreInstantiateBeans() {
        return getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();

        Stream.of(annotations)
                .forEach(annotation -> {
                    beans.addAll(reflections.getTypesAnnotatedWith(annotation));
                });

        return beans;
    }
}
