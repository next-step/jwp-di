package core.di.factory;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;

import com.google.common.collect.Maps;
import core.di.BeanDefinition;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private final BeanScanner beanScanner;

    public BeanFactory(Object... basePackage) {
        beanScanner = new BeanScanner(basePackage);
    }

    public void initialize(Class<? extends Annotation>... annotations) {
        beanDefinitions.putAll(beanScanner.scanAnnotatedWith(annotations));
        beanDefinitions.forEach((clazz, beandef) -> beans.put(clazz, instantiate(beandef)));
    }

    private Object instantiate(BeanDefinition beandef) {
        if (beans.containsKey(beandef.getBeanClass())) {
            return beans.get(beandef.getBeanClass());
        }

        if (beandef.getBeanConstructor() == null) {
            beans.put(beandef.getBeanClass(), instantiateBean(beandef));
            logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
            return beans.get(beandef.getBeanClass());
        }

        beans.put(beandef.getBeanClass(), instantiateConstructor(beandef.getBeanConstructor()));
        logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
        return beans.get(beandef.getBeanClass());
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

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanDefinitions.keySet()
                              .stream()
                              .filter(clazz -> clazz.isAnnotationPresent(annotation))
                              .collect(Collectors.toMap(clazz -> clazz, beans::get, (a, b) -> b));
    }
}
