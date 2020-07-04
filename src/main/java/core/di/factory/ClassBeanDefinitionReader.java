package core.di.factory;

import core.annotation.Inject;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;

public class ClassBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanFactory beanFactory;

    public ClassBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void loadBeanDefinitions(Class<?>... classes) {
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
