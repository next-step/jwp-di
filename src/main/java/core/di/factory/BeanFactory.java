package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        for (Class<?> clazz : preInstanticateBeans) {
            beans.put(clazz, findAndInitInject(clazz));
        }
    }

    private Object findAndInitInject(Class<?> clazz) {
        try {
            clazz = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
            return getNewInstance(clazz, BeanFactoryUtils.getInjectedConstructor(clazz));
        } catch (Exception e) {
            logger.error("injection Error {}", e.getMessage());
        }

        return null;
    }

    private Object getNewInstance(Class<?> clazz, Constructor constructor) throws Exception {
        return (constructor == null)
                ? clazz.newInstance()
                : constructor.newInstance(getConstructParameter(
                        constructor.getParameterTypes()));
    }

    private Object[] getConstructParameter(Object[] parameterObject){
        Object[] paramList = new Object[parameterObject.length];

        for (int i = 0; i < parameterObject.length; i++) {
            paramList[i] = findAndInitInject((Class<?>) parameterObject[i]);
        }

        return paramList;
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();

        List<Class<?>> controllerList = preInstanticateBeans.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toList());

        for (Class<?> clazz : controllerList){
            controllers.put(clazz, beans.get(clazz));
        }

        return controllers;
    }

}
