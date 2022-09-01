package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;

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

    public void initialize() {
        createDependencies();
    }

    private void createDependencies() {
        for (Class<?> clazz : preInstanticateBeans) {
            beans.putIfAbsent(clazz, recursive(clazz));
        }
    }

    private Object recursive(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();

        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < params.length; ++i) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterTypes[i], this.preInstanticateBeans);
            params[i] = recursive(concreteClass);
        }

        try {
            return injectedConstructor.newInstance(params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public List<Object> getControllers() {
        return this.beans.entrySet()
            .stream()
            .filter(classObjectEntry -> classObjectEntry.getKey()
                .isAnnotationPresent(Controller.class))
            .map(Entry::getValue)
            .collect(Collectors.toList());
    }

}
