package core.di.factory;

import core.annotation.Inject;
import core.mvc.tobe.MethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

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

        MethodParameter[] methodParameters = getMethodParameters(constructor);
        Object[] parameters = getParameters(beanFactory, methodParameters);

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
