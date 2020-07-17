package core.di.factory;

import com.google.common.collect.Maps;
import core.di.BeanDefinition;
import core.di.Beans;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private final Beans beans = new Beans();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private final BeanScanner beanScanner;

    public BeanFactory(Object... basePackage) {
        beanScanner = new BeanScanner(basePackage);
    }

    public void initialize(Class<? extends Annotation>... annotations) {
        beanDefinitions.putAll(beanScanner.scanAnnotatedWith(annotations));
        beans.instantiateBeans(beanDefinitions);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.debug("Get bean {}", requiredType.getName());
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanDefinitions.keySet()
                              .stream()
                              .filter(clazz -> clazz.isAnnotationPresent(annotation))
                              .collect(Collectors.toMap(clazz -> clazz, beans::get, (a, b) -> b));
    }
}
