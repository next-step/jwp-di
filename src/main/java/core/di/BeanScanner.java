package core.di;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class BeanScanner {

    private final Object[] basePackage;

    public BeanScanner(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public Set<Class<?>> scan(Class<? extends Annotation>... types) {
        return getTypesAnnotatedWith(types);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Reflections reflections = new Reflections(this.basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> classes = new HashSet<>();
        for(Class annotation:annotations){
            classes.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return classes;
    }

}
