package core.di.factory;

import core.di.scanner.ScannableAnnotaionTypes;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class ComponentTypeInitializer implements BeanInitializer {

    private final BeanFactory beanFactory;

    public ComponentTypeInitializer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean support(Object type) {
        return type instanceof Class && ScannableAnnotaionTypes.isScannable((Class<?>) type);
    }

    @Override
    public Object initialize(BeanRegistry beanRegistry, Object type) {
        Class<?> beanClass = (Class<?>) type;

        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, beanFactory.getAllBeanClasses());
        if (!beanFactory.containsType(concreteClass)) {
            throw new IllegalArgumentException("등록되지 않은 클래스 입니다. class : [" + beanClass + "]");
        }
        Object instance = newInstance(beanRegistry, concreteClass);
        beanRegistry.put(beanClass, instance);
        return instance;
    }

    private Object newInstance(BeanRegistry beanRegistry, Class<?> concreteClass) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(concreteClass);
        }

        return BeanUtils.instantiateClass(constructor, getConstructorParameterInstances(beanRegistry, constructor));
    }

    private Object[] getConstructorParameterInstances(BeanRegistry beanRegistry, Constructor<?> constructor) {
        Class<?>[] parameterClasses = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterClasses.length];
        for (int i = 0; i < parameterClasses.length; i++) {
            parameters[i] = getConstructorParameterInstance(beanRegistry, parameterClasses[i]);
        }
        return parameters;
    }

    private Object getConstructorParameterInstance(BeanRegistry beanRegistry, Class<?> parameterClass) {
        Object bean = beanRegistry.getBean(parameterClass);
        if (Objects.nonNull(bean)) {
            return bean;
        }
        return beanFactory.getBean(parameterClass);
    }
}
