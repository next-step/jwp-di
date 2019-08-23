package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Class<?> bean : preInstanticateBeans) {
            initializeBean(bean);
        }
    }

    public void initializeBean(Class<?> bean) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(bean);
        if (injectedConstructor == null) {
            beans.put(bean, bean.newInstance());
            return;
        }

        Parameter[] parameters = injectedConstructor.getParameters();
        List<Class<?>> concreteClassForParameters = new ArrayList<>();
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(type, preInstanticateBeans);
            concreteClassForParameters.add(concreteClass);
            initializeBean(concreteClass);
        }

        Object[] objects = concreteClassForParameters.stream()
                .map(clazz -> beans.get(clazz))
                .toArray();

        beans.put(bean, injectedConstructor.newInstance(objects));
    }
}
