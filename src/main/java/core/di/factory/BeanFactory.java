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

    void addBean(Map<? extends Class<?>, Object> beans) {
        beans.forEach((key, value) -> {
            logger.debug("Add bean to factory: {}", key.getName());
            this.beans.put(key, value);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
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
