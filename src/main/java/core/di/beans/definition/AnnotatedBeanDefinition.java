package core.di.beans.definition;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotatedBeanDefinition extends DefaultBeanDefinition {
    @Getter
    private Method method;

    public AnnotatedBeanDefinition(Class<?> type, Method method) {
        super(type);
        this.method = method;
    }

    @Override
    public boolean containsAnnotation(Class<? extends Annotation> annotationType) {
        return method.isAnnotationPresent(annotationType);
    }
}
