package core.di.scanner;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private static final List<Class<? extends Annotation>> ANNOTATIONS = List.of(Controller.class, Repository.class, Service.class);
    private final Reflections reflections;
    private final BeanFactory beanFactory;


    public BeanScanner(Object... basePackage) {
        reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        beanFactory = new BeanFactory(getPreInstantiateClazz());
        initialize();
    }

    private Set<Class<?>> getPreInstantiateClazz() {
        return ANNOTATIONS.stream()
                          .map(reflections::getTypesAnnotatedWith)
                          .flatMap(Set::stream)
                          .collect(Collectors.toSet());
    }

    private void initialize() {
        beanFactory.initialize();
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
