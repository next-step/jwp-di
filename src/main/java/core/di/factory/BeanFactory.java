package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Component;
import core.annotation.Inject;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;
import static core.di.factory.ReflectionSupport.getArguments;
import static core.di.factory.ReflectionSupport.setFieldByForce;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * Scanning and instantiate bean
 * contain bean map with Class key & Instance value
 */
@SuppressWarnings("unchecked")
public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(BeanScanner scanner) {
        this.preInstanticateBeans = scanner.scan(Controller.class, Service.class, Repository.class, Component.class);
    }

    public void initialize() {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            Object bean = getBean(preInstanticateBean);
            if (bean == null) {
                logger.warn("{} initiate failed. ", preInstanticateBean.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.info("find {}", requiredType);
        if (requiredType == null) {
            return null;
        }

        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }

        return createBean(requiredType);
    }

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(toMap(Entry::getKey, Entry::getValue, (b1, b2) -> b2));
    }

    @SuppressWarnings("unchecked")
    private <T> T createBean(Class<T> clazz) {
        Class<T> concreteClass = (Class<T>) findConcreteClass(clazz, preInstanticateBeans);

        if (!preInstanticateBeans.contains(concreteClass)) {
            return null;
        }

        T instance = newInstance(concreteClass);
        populateBean(instance, concreteClass);
        beans.put(clazz, instance);
        return instance;
    }

    private <T> T newInstance(final Class<T> clazz) {
        return ofNullable(getInjectedConstructor(clazz))
                .map(ctor -> instantiateClass(ctor, getArguments(ctor, this::getBean)))
                .orElseGet(() -> instantiateClass(clazz));
    }

    private void populateBean(final Object instance, final Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isInjectField)
                .forEach(field -> setFieldByForce(field, instance, getBean(field.getType())));
    }

    private boolean isInjectField(Field field) {
        return field != null
                && field.isAnnotationPresent(Inject.class)
                && !BeanUtils.isSimpleValueType(field.getClass());
    }

}
