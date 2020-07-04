package core.di.context;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.DefaultBeanFactory;
import core.di.factory.JavaConfigBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    private final BeanFactory beanFactory = new DefaultBeanFactory();

    public AnnotationConfigApplicationContext(Class<?>... configClasses) {
        // 1. find candidate packages to be scanned.
        final Object[] candidatePackages = findCandidatePackages(configClasses);

        // 2. load class-based definitions -> refactoring required.
        for (Object pkg : candidatePackages) {
            final BeanScanner bs = new BeanScanner(pkg);
            bs.loadBeanDefinitions(beanFactory);
        }

        // 3. load method-based definitions -> ㄷㄷㄷㄷ
        final JavaConfigBeanDefinitionReader definitionReader = new JavaConfigBeanDefinitionReader(beanFactory);
        definitionReader.loadBeanDefinitions(configClasses);

        beanFactory.initialize();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    private Object[] findCandidatePackages(Class<?>... configClasses) {
        final ArrayList<Object> packages = new ArrayList<>();
        for (Class<?> config : configClasses) {
            final ComponentScan componentScan = config.getAnnotation(ComponentScan.class);
            if (componentScan != null) {
                packages.addAll(Arrays.asList(componentScan.basePackages()));
            }
        }
        return packages.toArray();
    }
}
