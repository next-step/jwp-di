package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PreInstantiateBeans {
    private Set<Class<?>> preInstantiateBeans;

    public PreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    private static final Logger logger = LoggerFactory.getLogger(PreInstantiateBeans.class);
    public Object createBeanObject(Class<?> clazz) {
        try {
            Class conClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
            return conClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object createBeanServiceObject(Class<?> clazz) {
        try {
            Constructor constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            logger.debug("{}", constructor);

            List<Object> objects = new ArrayList<>();
            for (Class param : constructor.getParameterTypes()) {
                Object obj = createBeanObject(param);
                objects.add(obj);
            }

            return constructor.newInstance(objects.toArray());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object createBeanControllerObject(Class<?> clazz) {
        return null;
    }
}
