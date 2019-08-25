package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstantiateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(final Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeans() {
        return unmodifiableMap(beans);
    }

    public Map<Class<?>, Object> getBeansOfAnnotatedBy(final Class<? extends Annotation> annotation) {
        return unmodifiableMap(getBeans()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public void initialize() {
        preInstantiateBeans.forEach(this::initializeBean);
        logger.debug("Complete initialize [beanSize={}, beans={}]", beans.size(), beans);
    }

    private void initializeBean(final Class<?> clazz) {
        if (isAlreadyInitializeBean(clazz)) {
          return;
        }

        final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
        final Object instance = BeanFactoryUtils.getInjectedConstructor(concreteClass)
                .map(this::instantiate)
                .orElseGet(() -> BeanUtils.instantiateClass(concreteClass));

        beans.put(clazz, instance);
    }

    private boolean isAlreadyInitializeBean(final Class<?> clazz) {
        return beans.containsKey(clazz);
    }

    private Object instantiate(final Constructor<?> constructor) {
        final Object[] parameters = Arrays.stream(constructor.getParameters())
                .map(Parameter::getType)
                .peek(this::initializeBean)
                .map(this::getBean)
                .toArray();

        return BeanUtils.instantiateClass(constructor, parameters);
    }
}
