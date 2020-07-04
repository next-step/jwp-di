package core.di.context;

import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    private final BeanFactory beanFactory = new DefaultBeanFactory();

    public AnnotationConfigApplicationContext(Class<?>... configClasses) {
        // 1. find candidate packages to be scanned.
        final Object[] candidatePackages = findCandidatePackages(configClasses);

        // 2. load class-based definitions -> refactoring required.
        final BeanDefinitionReader classBeanDefinitionReader = new ClassBeanDefinitionReader(beanFactory);
        for (Object pkg : candidatePackages) {
            final BeanScanner bs = new BeanScanner(pkg);
            final Set<Class<?>> classes = bs.loadClasses(Controller.class, Service.class, Repository.class);
            classBeanDefinitionReader.loadBeanDefinitions(classes.toArray(new Class[classes.size()]));
        }

        // 3. load method-based definitions -> ㄷㄷㄷㄷ
        final BeanDefinitionReader javaConfigBeanDefinitionReader = new JavaConfigBeanDefinitionReader(beanFactory);
        javaConfigBeanDefinitionReader.loadBeanDefinitions(configClasses);

        beanFactory.initialize();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public Class<?>[] getBeanClasses() {
        return beanFactory.getBeanClasses();
    }

    private Object[] findCandidatePackages(Class<?>... configClasses) {
        final List<Object> packages = new ArrayList<>();
        for (Class<?> config : configClasses) {
            final ComponentScan componentScan = config.getAnnotation(ComponentScan.class);
            if (componentScan != null) {
                packages.addAll(Arrays.asList(componentScan.basePackages()));
            }
        }
        return packages.toArray();
    }
}
