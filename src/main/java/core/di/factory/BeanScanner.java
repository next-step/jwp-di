package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.di.BeanDefinition;
import core.di.BeanDefinitionImpl;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    private final Reflections reflections;

    public BeanScanner(Object... basePackage) {
        this.reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
    }

    public Map<Class<?>, BeanDefinition> scanAnnotatedWith(Class<? extends Annotation>... annotations) {
        Map<Class<?>, BeanDefinition> beanDefinitionMap = Maps.newHashMap();
        Set<Class<?>> preInstantiateBeans = getTypesAnnotatedWith(annotations);
        for (Class<?> clazz : preInstantiateBeans) {
            beanDefinitionMap.put(clazz, new BeanDefinitionImpl(clazz));
        }
        return beanDefinitionMap;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        logger.debug("Found {} Classes", beans.size());
        return beans;
    }
}
