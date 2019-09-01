package core.di.factory;

import java.lang.annotation.Annotation;

public class ComponentBeanDefinition implements BeanDefinition {
    private final Class<?> clazz;

    public ComponentBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getBeanType() {
        return clazz;
    }

    @Override
    public boolean isAnnotation(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }
}
