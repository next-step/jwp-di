package core.di;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.WebApplication;
import core.annotation.web.Controller;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class BeanScanner {

    private static final Class<WebApplication> WEB_APPLICATION_ANNOTATION = WebApplication.class;

    private Object[] basePackage;
    private Reflections reflections;
    private Set<Class<?>> preInstantiateBeans;

    public void initialize() {
        Reflections wholeReflections = new Reflections("");
        this.basePackage = wholeReflections.getTypesAnnotatedWith(WEB_APPLICATION_ANNOTATION)
                .stream()
                .map(this::findBasePackages)
                .flatMap(Arrays::stream)
                .toArray();

        if (this.basePackage.length == 0) {
            throw new IllegalStateException("Base package not initialized");
        }
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> scan() {
        this.reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> beans = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        this.preInstantiateBeans = beans;
        return beans;
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    private String[] findBasePackages(Class<?> clazz) {
        String[] packages = clazz.getAnnotation(WEB_APPLICATION_ANNOTATION).basePackages();

        return packages.length == 0 ? new String[]{clazz.getPackage().getName()} : packages;
    }
}
