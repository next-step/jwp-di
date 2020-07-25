package core.di;

import core.di.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ApplicationContext {

    private final BeanFactory beanFactory;

    public ApplicationContext(Object... basePackage) {
        if (isEmpty(basePackage)) {
            ComponentBasePackageScanner basePackageScanner = new ComponentBasePackageScanner();
            basePackage = basePackageScanner.scan().toArray();
        }

        beanFactory = new BeanFactory();

        BeanScanner beanScanner = new BeanScanner(beanFactory);
        beanScanner.scan(basePackage);

        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.scan(basePackage);

        beanFactory.initialize();
    }

    public Set<Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansAnnotatedWith(annotation);
    }


    private boolean isEmpty(Object[] basePackage) {
        return basePackage == null || basePackage.length == 0;
    }

}
