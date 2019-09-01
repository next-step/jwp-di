package core.di.context;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.AnnotationBeanScanner;

import java.util.Map;

public class AnnotationConfigApplicationContext {

    private String[] basePackages;
    private AnnotationBeanScanner annotationBeanScanner;
    private BeanScanner beanScanner;

    private BeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Class<?> clazz) {
        this.basePackages = parseBasePackages(clazz);
        this.beanFactory = new BeanFactory();

        this.annotationBeanScanner = new AnnotationBeanScanner(clazz);

        if (basePackages.length > 0) {
            this.beanScanner = new BeanScanner(basePackages);
            this.beanScanner.scan(this.beanFactory);
        }

        this.beanFactory.initialize();
    }

    private String[] parseBasePackages(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Configuration.class) && clazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = clazz.getDeclaredAnnotation(ComponentScan.class);
            return getBasePackages(componentScan);
        }
        return new String[0];
    }

    private String[] getBasePackages(ComponentScan componentScan) {
        if (componentScan == null) {
            return new String[0];
        }

        String[] basePackages = componentScan.value();
        if (basePackages.length > 0) {
            return basePackages;
        }

        return componentScan.basePackages();
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }
}
