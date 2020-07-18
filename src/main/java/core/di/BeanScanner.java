package core.di;

import com.google.common.collect.Sets;
import core.annotation.Component;
import core.di.factory.BeanInstantiationUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanScanner implements Scanner<Class<?>> {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";
    private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

    private Reflections reflections = new Reflections("");
    private Set<Class<?>> preInstantiateBeans;

    public BeanScanner(Object... basePackage) {
        this.reflections = new Reflections(basePackage);
    }

    @Override
    public Set<Class<?>> scan() {
        Set<Class<? extends Annotation>> componentAnnotations = getComponentAnnotations();
        this.preInstantiateBeans = getTypesAnnotatedWith(componentAnnotations);
        return preInstantiateBeans;
    }

    public boolean contains(Class<?> preInstantiateBean) {
        return preInstantiateBeans.contains(preInstantiateBean);
    }

    public Class<?> findConcreteClass(Class<?> preInstantiateBean) {
        return BeanInstantiationUtils.findConcreteClass(preInstantiateBean, preInstantiateBeans);
    }

    private Set<Class<? extends Annotation>> getComponentAnnotations() {
        Reflections annotationReflections = new Reflections(ANNOTATION_BASE_PACKAGE);
        Set<Class<?>> componentClasses = annotationReflections.getTypesAnnotatedWith(COMPONENT_ANNOTATION);

        Set<Class<? extends Annotation>> annotations = componentClasses.stream()
                .filter(Class::isAnnotation)
                .map(clazz -> (Class<? extends Annotation>) clazz)
                .collect(Collectors.toSet());

        annotations.add(COMPONENT_ANNOTATION);
        return annotations;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Set<Class<? extends Annotation>> annotations) {
        Set<Class<?>> annotatedClasses = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            annotatedClasses.addAll(this.reflections.getTypesAnnotatedWith(annotation, true));
        }
        return annotatedClasses;
    }

}
