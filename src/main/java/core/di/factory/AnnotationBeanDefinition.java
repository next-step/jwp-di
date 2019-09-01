package core.di.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationBeanDefinition implements BeanDefinition {

    private Method method;

    public AnnotationBeanDefinition(Method method) {
        this.method = method;
    }

    public Object getInstanceClass() throws IllegalAccessException, InstantiationException {
        return method.getDeclaringClass().newInstance();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameters() {
        return method.getParameterTypes();
    }

    @Override
    public Class<?> getBeanType() {
        return method.getReturnType();
    }

    @Override
    public boolean isAnnotation(Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation);
    }
}
