package core.di;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;

import com.google.common.collect.Maps;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Created by iltaek on 2020/07/17 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class Beans {

    private static final Logger logger = LoggerFactory.getLogger(Beans.class);
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    public void instantiateBeans(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions.putAll(beanDefinitions);
        beanDefinitions.forEach((clazz, beandef) -> beans.put(clazz, instantiate(beandef)));
    }

    public Object get(Class<?> beanClass) {
        return beans.get(beanClass);
    }

    private Object instantiate(BeanDefinition beandef) {
        if (isPresent(beandef.getBeanClass())) {
            return beans.get(beandef.getBeanClass());
        }

        if (beandef.getBeanConstructor() == null) {
            logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
            return putAndGet(beandef.getBeanClass(), instantiateBean(beandef));
        }

        logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
        return putAndGet(beandef.getBeanClass(), instantiateConstructor(beandef.getBeanConstructor()));
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> list = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            Class<?> parameterClass = findConcreteClass(parameterType, beanDefinitions.keySet());
            Object instantiate = instantiate(beanDefinitions.get(parameterClass));
            list.add(instantiate);
        }
        return BeanUtils.instantiateClass(constructor,
                                          list.toArray());
    }

    private Object instantiateBean(BeanDefinition beandef) {
        return BeanUtils.instantiateClass(findConcreteClass(beandef.getBeanClass(), beanDefinitions.keySet()));
    }

    private boolean isPresent(Class<?> beanClass) {
        return beans.containsKey(beanClass);
    }

    private Object putAndGet(Class<?> beanClass, Object bean) {
        beans.put(beanClass, bean);
        return beans.get(beanClass);
    }
}
