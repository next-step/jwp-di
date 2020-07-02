package core.di.factory;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;

@Slf4j
public class ClasspathBeanScanner {
    public static final Class<? extends Annotation> [] CLASSPATH_TARGET_TYPES = new Class[] {Controller.class, Service.class, Repository.class, Component.class};
    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(String... basePackages) {
        if (ArrayUtils.isNotEmpty(basePackages)) {
            registerBasePackages(basePackages);
        }

        initializeBeanDefinitions();
    }

    private void registerBasePackages(String[] basePackages) {
        beanFactory.registerBasePackages(basePackages);
    }

    private void initializeBeanDefinitions() {
        beanFactory.registerBeanDefinitions(beanFactory.getClassPathRootTypes());
    }
}
