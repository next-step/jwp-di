package core.di.tobe;

import core.annotation.ComponentScan;
import core.di.tobe.scanner.BeanScanner;
import core.di.tobe.scanner.ConfigurationBeanScanner;
import core.di.tobe.scanner.DefaultBeanScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);

    private List<BeanScanner> beanScanners;
    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?>... clazz) {
        Object[] packages = findDefaultPackages(clazz);
        log.debug("Component scan packages : {} ", packages);

        beanFactory = DefaultBeanFactory.getInstance();
        beanScanners = Arrays.asList(new ConfigurationBeanScanner(beanFactory, packages),
                new DefaultBeanScanner(beanFactory, packages));

        beanScanners.forEach(BeanScanner::enroll);
        beanFactory.initialize();
    }

    private Object[] findDefaultPackages(Class<?>[] annotatedClasses) {
        return Arrays.stream(annotatedClasses)
                .filter(aClass -> aClass.isAnnotationPresent(ComponentScan.class))
                .map(aClass -> aClass.getAnnotation(ComponentScan.class))
                .map(ComponentScan::value)
                .toArray();
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotations) {
        return beanFactory.getBeans(annotations);
    }

}
