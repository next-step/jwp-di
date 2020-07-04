package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Inject;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
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

    @SafeVarargs
    public final Set<Class<?>> loadClasses(Class<? extends Annotation>... annotations) {
        final Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            final Set<Class<?>> classes = loadClasses(annotation);
            beans.addAll(classes);
        }
        return beans;
    }

    // TODO: 모든 작업 끝난 후 분리 고민해볼 것
    public void loadBeanDefinitions(BeanFactory beanFactory) {
        final Set<Class<?>> classes = this.loadClasses(Controller.class, Service.class, Repository.class);
        for (Class<?> clazz : classes) {
            final BeanDefinition beanDefinition = buildBeanDefinition(clazz);
            beanFactory.registerBeanDefinition(clazz, beanDefinition);
            for (Class<?> type : clazz.getInterfaces()) {
                beanFactory.registerBeanDefinition(type, beanDefinition);
            }
        }
    }

    private BeanDefinition buildBeanDefinition(Class<?> clazz) {
        final ClassBeanDefinition beanDefinition = new ClassBeanDefinition(clazz);
        final Constructor<?> ctor = findBeanConstructor(clazz);
        if (ctor != null) {
            beanDefinition.setDependencies(ctor.getParameterTypes());
            beanDefinition.setBeanConstructor(ctor);
        }
        return beanDefinition;
    }

    @Nullable
    private Constructor<?> findBeanConstructor(Class<?> clazz) {
        final Constructor<?>[] ctors = clazz.getDeclaredConstructors();
        Constructor<?> nonArgsCtor = null;
        for (Constructor<?> ctor : ctors) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                return ctor;
            }

            if (ctor.getParameterTypes().length == 0) {
                nonArgsCtor = ctor;
            }
        }
        return nonArgsCtor;
    }
}
