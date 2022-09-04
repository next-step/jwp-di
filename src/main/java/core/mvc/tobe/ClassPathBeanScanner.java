package core.mvc.tobe;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class ClassPathBeanScanner {

    private ClassPathBeanScanner() {
    }

    public static Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        List<Class<? extends Annotation>> annotationClasses = List.of(Controller.class, Service.class, Repository.class);
        Set<Class<?>> preInstantiateBeans = Sets.newHashSet();
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            preInstantiateBeans.addAll(reflections.getTypesAnnotatedWith(annotationClass));
        }
        return preInstantiateBeans;
    }
}
