package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;

        try {
            BeanMaker beanMaker = new BeanMaker();

            for (Class<?> clazz : preInstanticateBeans) {
                addBean(clazz, findAndInitInject(clazz));
                addBeans(beanMaker.findAndInitBean(clazz, beans));
            }
        } catch (BeanMakerException e) {
            logger.error("Bean Create Initialize Error {}", e.getMessage());
        }
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();

        List<Class<?>> controllerList = preInstanticateBeans.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toList());

        for (Class<?> clazz : controllerList) {
            controllers.put(clazz, beans.get(clazz));
        }

        return controllers;
    }

    private void addBean(Class<?> clazz, Object value) {
        if (!beans.containsKey(clazz) || beans.get(clazz) == null) {
            beans.put(clazz, value);
            return;
        }
    }

    private void addBeans(Map<Class<?>, Object> lists) {
        for (Class<?> clazz : lists.keySet()) {
            addBean(clazz, beans.get(clazz));
        }
    }

    public Object findAndInitInject(Class<?> clazz) {
        Object returnObject = null;
        try {
            clazz = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);

            if(beans.containsKey(clazz)){
                return beans.get(clazz);
            }

            returnObject = getNewInstance(clazz, BeanFactoryUtils.getInjectedConstructor(clazz));
        } catch (Exception e) {
            logger.error("injection Error {}", e.getMessage());
        }

        return returnObject;
    }

    private Object getNewInstance(Class<?> clazz, Constructor constructor) throws Exception {
        return (constructor == null)
                ? clazz.newInstance()
                : constructor.newInstance(getConstructParameter(
                constructor.getParameterTypes()));
    }

    private Object[] getConstructParameter(Object[] parameterObject) {
        Object[] paramList = new Object[parameterObject.length];

        for (int i = 0; i < parameterObject.length; i++) {
            paramList[i] = findAndInitInject((Class<?>) parameterObject[i]);
        }

        return paramList;
    }

}
