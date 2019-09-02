package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AnnotationBeanDefinition implements BeanDefinition {
    private Logger logger = LoggerFactory.getLogger(AnnotationBeanDefinition.class);

    private Method method;

    public AnnotationBeanDefinition(Method method) {
        this.method = method;
    }

    @Override
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

    @Override
    public Object newInstance(List<Object> parameters) {
        try {
            return method.invoke(getInstanceClass(), parameters.toArray());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.error("Bean 생성 실패 ", e);
            throw new RuntimeException("Bean 생성 실패 ", e);
        }
    }

    private Object getInstanceClass() throws IllegalAccessException, InstantiationException {
        return method.getDeclaringClass().newInstance();
    }
}
