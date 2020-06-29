package core.di.factory;

import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Bean 클래스를 찾는 책임을 갖는 객체
 *
 * @author chwon
 */
public class BeanScanner {

    private final Reflections reflections;
    private final Map<Class<? extends Annotation>, Set<Class<?>>> cache = new HashMap<>();

    public BeanScanner(Object... basePackages) {
        reflections = new Reflections(ConfigurationBuilder
                .build(basePackages)
                .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
        );
    }

    public Set<Class<?>> loadClasses(Class<? extends Annotation> annotation) {
        final Optional<Set<Class<?>>> maybeClasses = Optional.ofNullable(cache.get(annotation));
        if (maybeClasses.isPresent()) {
            return maybeClasses.get();
        }

        final Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
        cache.put(annotation, classes);
        return classes;
    }

    public Set<Class<?>> loadClasses(Class<? extends Annotation>... annotations) {
        final Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            final Set<Class<?>> classes = loadClasses(annotation);
            beans.addAll(classes);
        }
        return beans;
    }
}
