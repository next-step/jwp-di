package core.di.factory.container;

import core.annotation.web.Controller;
import core.di.factory.scanner.ClassPathBeanScanner;
import core.di.factory.scanner.ConfigurationBeanScanner;
import core.di.factory.constructor.BeanConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final Object[] basePackages;
    private BeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Object... basePackages) {
        Assert.notNull(basePackages, "basePackages가 null이어선 안됩니다.");
        this.basePackages = basePackages;
    }

    public void initialize() {
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(basePackages);
        ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner(configurationBeanScanner.configurations());
        Collection<BeanConstructor> constructors = new ArrayList<>();
        constructors.addAll(configurationBeanScanner.scan());
        constructors.addAll(classPathBeanScanner.scan());
        beanFactory = new BeanFactory(constructors);
        beanFactory.initialize();
    }

    public Collection<Object> getControllers() {
        return beanFactory.beansAnnotatedWith(Controller.class);
    }
}
