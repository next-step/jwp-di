package core.di.factory;

import core.di.BeanDefinitions;
import core.di.Beans;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private final Beans beans = new Beans();
    private final BeanDefinitions beanDefinitions;

    public BeanFactory(Object... basePackage) {
        beanDefinitions = new BeanDefinitions(new BeanScanner(basePackage));
    }

    public void initialize(Class<? extends Annotation>... annotations) {
        beanDefinitions.addAnnotatedWith(annotations);
        beans.instantiateBeans(beanDefinitions);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.debug("Get bean {}", requiredType.getName());
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanDefinitions.getPreInstantiateBeans()
                              .stream()
                              .filter(clazz -> clazz.isAnnotationPresent(annotation))
                              .collect(Collectors.toMap(clazz -> clazz, beans::get, (a, b) -> b));
    }
}
