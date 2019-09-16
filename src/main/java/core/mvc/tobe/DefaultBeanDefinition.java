package core.mvc.tobe;

import core.di.factory.BeanFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;
import support.exception.NoSuchDefaultConstructorException;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.Function;

public class DefaultBeanDefinition implements BeanDefinition {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanDefinition.class);

    private Class<?> beanClass;
    private Constructor<?> constructor;
    private Class<?>[] parameterTypes;

    public DefaultBeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.constructor = getConstructor(beanClass);
        this.parameterTypes = this.constructor.getParameterTypes();
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        return getDefaultConstructor(clazz);
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            throw new NoSuchDefaultConstructorException();
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    @Override
    public Function<Object[], Object> getInstantiateFunction() {
        return (parameters) -> {
            try {
                return constructor.newInstance(parameters);
            } catch (ReflectiveOperationException e) {
                logger.error(e.getMessage());
                throw new CreateInstanceFailException();
            }
        };
    }
}
