package core.di.scanner;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.bean.AnnotationBeanDefinition;
import core.di.bean.BeanDefinition;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class AnnotationBeanScanner implements BeanScanner {

    private static final Class<? extends Annotation> ANNOTATION_OF_CONFIGURATION = Configuration.class;
    
    private final Reflections reflection;

    public AnnotationBeanScanner(Object... basePackage) {
        this.reflection = new Reflections(basePackage);
    }

    public Set<BeanDefinition> scan() {
        Set<BeanDefinition> beanDefinitions = Sets.newHashSet();

        Set<Class<?>> annotatedWith = getTypesAnnotatedWith(ANNOTATION_OF_CONFIGURATION);
        for (Class<?> clazz : annotatedWith) {
            Set<Method> methods = getMethods(clazz, withAnnotation(Bean.class));
            for (Method method : methods) {
                beanDefinitions.add(new AnnotationBeanDefinition(clazz, method));
            }
        }
        return beanDefinitions;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflection.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
