package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.di.BeanDefinition;
import core.di.ClasspathBeanDefinition;
import core.di.BeanDefinitions;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathBeanDefinitionScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathBeanDefinitionScanner.class);

    private final BeanDefinitionRegistry beanDefinitionRegistry;
    private List<Class<? extends Annotation>> annotations;
    private Reflections reflections;

    public ClasspathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void setAnnotations(Class<? extends Annotation>... annotations) {
        this.annotations = Arrays.asList(annotations);
    }

    public void doScan(Object... basePackage) {
        this.reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        beanDefinitionRegistry.registerBeanDefinitions(scanAnnotatedWith(annotations));
    }

    private BeanDefinitions scanAnnotatedWith(List<Class<? extends Annotation>> annotations) {
        Map<Class<?>, BeanDefinition> beanDefinitionMap = Maps.newHashMap();
        Set<Class<?>> preInstantiateBeans = getTypesAnnotatedWith(annotations);
        for (Class<?> clazz : preInstantiateBeans) {
            beanDefinitionMap.put(clazz, new ClasspathBeanDefinition(clazz));
        }
        return BeanDefinitions.fromMap(beanDefinitionMap);
    }

    private Set<Class<?>> getTypesAnnotatedWith(List<Class<? extends Annotation>> annotations) {
        Set<Class<?>> beanClazz = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beanClazz.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        logger.debug("Found {} Classes", beanClazz.size());
        return beanClazz;
    }
}
