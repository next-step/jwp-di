package core.di.factory;

import core.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author KingCjy
 */
public class ClassBeanDefinitionInitializer extends AbstractBeanDefinitionInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ClassBeanDefinitionInitializer.class);

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition instanceof ClassBeanDefinition;
    }

    @Override
    public Object instantiateBean(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        Constructor<?> constructor = findInjectController(beanDefinition.getType());

        Object[] parameters = BeanFactoryUtils.getParameters(beanFactory, constructor);

        Object instance = BeanUtils.instantiateClass(constructor, parameters);
        logger.info("bean " + instance.getClass() + " instantiate");

        return instance;
    }

    private Constructor<?> findInjectController(Class<?> targetClass) {
        return Arrays.stream(targetClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .findAny()
                .orElseGet(() -> findPrimaryConstructor(targetClass));
    }

    private Constructor<?> findPrimaryConstructor(Class<?> targetClass) {
        try {
            return targetClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanInstantiationException(targetClass, "Constructor with @Inject not Found");
        }
    }
}
