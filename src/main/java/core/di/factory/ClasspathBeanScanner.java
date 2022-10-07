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
import java.util.Map;
import java.util.Set;

class ClasspathBeanScanner {

    private final BeanFactory beanFactory;

    ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    ClasspathBeanScanner(BeanFactory beanFactory, Object... basePackage) {
        this.beanFactory = beanFactory;
        doScan(basePackage);
    }

    void doScan(Object... basePackage) {
        Set<Class<?>> beans = Sets.newHashSet();
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        beans.addAll(getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class));

        beanFactory.setPreInstanticateBeans(beans);
        beanFactory.initialize();
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    public Map<Class<?>, Object> getFactoryController() {
        return beanFactory.getControllers();
    }
}
