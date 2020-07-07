package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final BeanScanner beanScanner;
    private Set<Class<?>> preInstantiateBeans;

    public BeanFactory(Object... basePackage) {
        beanScanner = new BeanScanner(basePackage);
    }

    public void initialize(Class<? extends Annotation>... annotations) {
        preInstantiateBeans = beanScanner.getTypesAnnotatedWith(annotations);
        preInstantiateBeans.forEach(clazz -> beans.put(clazz, instantiate(clazz)));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    private Object instantiate(Class<?> clazz) {
        logger.debug("Instantiate {} class", clazz.getName());
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            beans.put(clazz, instantiateClass(clazz));
            return beans.get(clazz);
        }

        beans.put(clazz, instantiateConstructor(injectedConstructor));
        return beans.get(clazz);
    }

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
        return BeanUtils.instantiateClass(concreteClass);
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : parameterTypes) {
            args.add(instantiate(clazz));
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        Map<Class<?>, Object> map = new HashMap<>();
        for (Class<?> clazz : preInstantiateBeans) {
            if (clazz.isAnnotationPresent(annotation)) {
                map.put(clazz, beans.get(clazz));
            }
        }
        return map;
    }
}
