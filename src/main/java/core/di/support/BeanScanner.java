package core.di.support;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);
    private static final List<Class<? extends Annotation>> ANNOTATIONS_OF_BEAN = List.of(Controller.class,
            Repository.class, Service.class);

    private final BeanFactory beanFactory;

    public BeanScanner(final Object... basePackage) {
        final Reflections reflections = new Reflections(basePackage);
        final Set<Class<?>> preInstantiateClazz = ANNOTATIONS_OF_BEAN.stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(toSet());

        logger.debug("Load preInstantiateClazz [basePackage={}, annotationsOfBean={}, preInstantiateClazz={}]",
                basePackage, ANNOTATIONS_OF_BEAN, preInstantiateClazz);

        beanFactory = new BeanFactory(preInstantiateClazz);
        beanFactory.initialize();
    }

    public Map<Class<?>, Object> getBeansOfAnnotatedBy(final Class<? extends Annotation> annotation) {
        return beanFactory.getBeansOfAnnotatedBy(annotation);
    }
}
