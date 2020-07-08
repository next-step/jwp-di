package core.di.factory;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

import com.google.common.collect.Maps;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

        Constructor<?> injectedConstructor = getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            beans.put(clazz, instantiateClass(clazz));
            return beans.get(clazz);
        }

        beans.put(clazz, instantiateConstructor(injectedConstructor));
        return beans.get(clazz);
    }

    private Object instantiateClass(Class<?> clazz) {
        return BeanUtils.instantiateClass(findConcreteClass(clazz, preInstantiateBeans));
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return BeanUtils.instantiateClass(constructor,
                                          Arrays.stream(parameterTypes)
                                                .map(this::instantiate)
                                                .toArray());
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return preInstantiateBeans.stream()
                                  .filter(clazz -> clazz.isAnnotationPresent(annotation))
                                  .collect(Collectors.toMap(clazz -> clazz, beans::get, (a, b) -> b));
    }
}
