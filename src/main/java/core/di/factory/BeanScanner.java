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

public class BeanScanner {
    private static final Class<? extends Annotation>[] SUPPORTED_BEAN_ANNOTATION =
            new Class[]{Controller.class, Service.class, Repository.class};

    private BeanFactory beanFactory = null;

    public void scan(Object... basePackages) {
        Reflections reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> preInstanticateClazz = Sets.newHashSet();
        for (Class<? extends Annotation> beanAnnotation : SUPPORTED_BEAN_ANNOTATION) {
            preInstanticateClazz.addAll(reflections.getTypesAnnotatedWith(beanAnnotation));
        }

        this.beanFactory = new BeanFactory(preInstanticateClazz);
        this.beanFactory.initialize();
    }

    public <T> T getBean(Class<T> clazz) {
        return this.beanFactory.getBean(clazz);
    }
}
