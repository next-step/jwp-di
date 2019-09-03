package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class ComponentBeanDefinition implements BeanDefinition {
    private Logger logger = LoggerFactory.getLogger(ComponentBeanDefinition.class);

    private final Class<?> clazz;
    private final Constructor constructor;

    public ComponentBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
        this.constructor = findConstructor(clazz);
    }

    private Constructor findConstructor(Class<?> clazz) {
        Constructor constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (constructor != null) {
            return constructor;
        }

        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Constructor를 찾지 못했습니다.");
        }
    }

    @Override
    public Class<?> getBeanType() {
        return clazz;
    }

    @Override
    public Class<?>[] getParameters() {
        return this.constructor.getParameterTypes();
    }

    @Override
    public boolean isAnnotation(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    @Override
    public Object newInstance(List<Object> parameters) {
        try {
            this.constructor.setAccessible(true);
            return this.constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("객체 생성 실패 ", e);
            throw new RuntimeException("객체 생성 실패 ", e);
        }
    }
}
