package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
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
            logger.error("bean create error : bean type={}", preInstantiateBean.getTypeName(), e);
            throw new RuntimeException();
        }
    }

    private Object registerBean(Class<?> beanType) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        Object bean;
        Constructor<?> injectableConstructor = getInjectedConstructor(beanType);
        if (injectableConstructor == null) {
            bean = beanType.newInstance();
            beans.put(beanType, bean);
            return bean;
        }

        Parameter[] parameters = injectableConstructor.getParameters();
        Object[] dependencies = getDependencies(parameters);

        bean = injectableConstructor.newInstance(dependencies);
        beans.put(beanType, bean);
        return bean;
    }

    private Object[] getDependencies(Parameter[] parameters) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> argumentType = findConcreteClass(parameters[i].getType(), preInstantiateBeans);
            Object argument = getDependentBean(argumentType);
            arguments[i] = argument;
        }
        return arguments;
    }

    private Object getDependentBean(Class<?> argumentType) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object dependentBean = getBean(argumentType);
        if (dependentBean == null) {
            return registerBean(argumentType);
        }
        return dependentBean;
    }
}
