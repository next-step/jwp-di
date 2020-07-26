package core.di;

import com.google.common.collect.Sets;
import core.annotation.AnnotationScanner;
import core.annotation.Component;
import core.di.factory.BeanFactory;
import core.di.factory.DefaultBeanDefinition;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class ClasspathBeanScanner implements BeanScanner<Class<?>> {

    private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

    private BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Set<Class<?>> scan(Object... basePackage) {
        Set<Class<? extends Annotation>> componentAnnotations = getComponentAnnotations();
        Set<Class<?>> preInstantiateBeans = getTypesAnnotatedWith(componentAnnotations, basePackage);

        for (Class<?> clazz : preInstantiateBeans) {
            this.beanFactory.registerBeanDefinition(clazz, new DefaultBeanDefinition(clazz));
        }

        return preInstantiateBeans;
    }

    private Set<Class<? extends Annotation>> getComponentAnnotations() {
        AnnotationScanner annotationScanner = new AnnotationScanner();

        return annotationScanner.scan(COMPONENT_ANNOTATION);
    }

    private Set<Class<?>> getTypesAnnotatedWith(Set<Class<? extends Annotation>> annotations, Object[] basePackage) {
        Reflections reflections = new Reflections(basePackage);

        Set<Class<?>> annotatedClasses = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            annotatedClasses.addAll(reflections.getTypesAnnotatedWith(annotation, true));
        }

        return annotatedClasses;
    }

}
