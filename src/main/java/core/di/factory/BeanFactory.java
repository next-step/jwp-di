package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
public class BeanFactory {
    private static final Class CONTROLLER_CLASS = Controller.class;
    private static final Class[] TARGET_BEAN_CLASSES = new Class[]{Controller.class, Service.class, Repository.class};

    private final Reflections reflections;
    private final Set<Class<?>> preInstanticateBeans;
    private final List<BeanGetter> beanGetters;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(String ...basePackages) {
        this.reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        this.preInstanticateBeans = ReflectionUtils.getTypesAnnotatedWith(reflections, TARGET_BEAN_CLASSES);
        this.beanGetters = Arrays.asList(new ConstructorBeanGetter(preInstanticateBeans, beans));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            for (BeanGetter beanGetter : beanGetters) {
                beans.put(beanClass, beanGetter.getBean(beanClass));
            }
        }

        beans.keySet().forEach(
            clazz -> log.debug("beanClassName: {}", clazz.getSimpleName())
        );
    }

    public Set<? extends Map.Entry<Class<?>, Object>> getControllers() {
        return beans.entrySet()
                .stream()
                .filter(bean -> bean.getKey().isAnnotationPresent(CONTROLLER_CLASS))
                .collect(toSet());
    }
}