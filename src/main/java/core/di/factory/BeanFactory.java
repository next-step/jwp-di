package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    void addBean(Object bean) {
        logger.debug("Add bean to factory: {}", bean.getClass().getName());
        this.beans.put(bean.getClass(), bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        final T bean = (T) beans.get(requiredType);
        if (bean != null) {
            return bean;
        }
        return (T) beans.get(BeanFactoryUtils.findConcreteClass(requiredType, beans.keySet()));
    }

    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    public Map<Class<?>, Object> getControllers() {
        return beans.keySet()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(clazz -> clazz, beans::get));
    }

    public void initialize() {

    }
}
