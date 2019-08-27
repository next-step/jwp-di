package core.di.factory;

import com.google.common.collect.Maps;
import core.di.exception.BeanInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            initializeBean(preInstantiateBean);
        }
        logger.debug("beans: {}", beans);
    }

    private void initializeBean(Class<?> preInstantiateBean) {
        try {
            registerBean(preInstantiateBean);
        } catch (ReflectiveOperationException e) {
            logger.error("bean instantiate exception : type = {}", preInstantiateBean.getTypeName(), e);
            throw new BeanInstantiationException(preInstantiateBean, e);
        }
    }

    private Object registerBean(Class<?> beanType) throws ReflectiveOperationException {

        Constructor<?> injectableConstructor = getInjectedConstructor(beanType);
        if (injectableConstructor == null) {
            return instantiateClass(beanType);
        }

        return instantiateConstructor(injectableConstructor);
    }

    private Object instantiateClass(Class<?> clazz) throws ReflectiveOperationException {
        Object bean = clazz.newInstance();
        beans.put(clazz, bean);
        return bean;
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws ReflectiveOperationException {
        Parameter[] parameters = constructor.getParameters();
        Object[] dependencies = getDependencies(parameters);
        Object bean = constructor.newInstance(dependencies);
        beans.put(constructor.getDeclaringClass(), bean);
        return bean;
    }

    private Object[] getDependencies(Parameter[] parameters) throws ReflectiveOperationException {
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> argumentType = findConcreteClass(parameters[i].getType(), preInstantiateBeans);
            Object argument = getDependentBean(argumentType);
            arguments[i] = argument;
        }
        return arguments;
    }

    private Object getDependentBean(Class<?> dependentBeanType) throws ReflectiveOperationException {
        return Optional.ofNullable((Object) getBean(dependentBeanType))
                .orElse(registerBean(dependentBeanType));
    }
}
