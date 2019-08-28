package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.di.exception.BeanInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            Object bean = beanType.newInstance();
            beans.put(beanType, bean);
            return bean;
        }

        Object bean = instantiateConstructor(injectableConstructor);
        beans.put(beanType, bean);
        return bean;
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws ReflectiveOperationException {
        Parameter[] parameters = constructor.getParameters();
        Object[] dependencies = getDependencies(parameters);
        return constructor.newInstance(dependencies);
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
        Object bean = getBean(dependentBeanType);
        if (bean == null) {
            return registerBean(dependentBeanType);
        }
        return bean;
    }

    public Map<Class<?>, Object> getControllers() {
        return beans.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
