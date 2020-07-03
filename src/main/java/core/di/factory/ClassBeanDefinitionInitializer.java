package core.di.factory;

import core.annotation.Inject;
import core.mvc.tobe.MethodParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

/**
 * @author KingCjy
 */
public class ClassBeanDefinitionInitializer extends AbstractBeanDefinitionInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ClassBeanDefinitionInitializer.class);

    @Override
    public boolean support(BeanDefinition beanDefinition) {
        return beanDefinition instanceof ClassBeanDefinition;
    }

    @Nullable
    @Override
    public Object instantiateBean(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        Constructor constructor = findInjectController(beanDefinition.getType());

        if(constructor == null) {
            throw new BeanInstantiationException(beanDefinition.getType(), "Constructor with @Inject not Found");
        }

        MethodParameter[] methodParameters = getMethodParameters(constructor);
        Object[] parameters = getParameters(beanFactory, methodParameters);

        Object instance = BeanUtils.instantiateClass(constructor, parameters);
        logger.info("bean " + instance.getClass() + " instantiate");

        return instance;
    }

    private Constructor findInjectController(Class<?> targetClass) {
        for (Constructor<?> constructor : targetClass.getConstructors()) {
            if(constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }

        try {
            return targetClass.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}