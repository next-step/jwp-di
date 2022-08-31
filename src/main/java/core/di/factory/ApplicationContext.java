package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.constructor.BeanConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

public final class ApplicationContext {

    private final Object[] basePackages;
    private BeanFactory beanFactory;

    private ApplicationContext(Object... basePackages) {
        Assert.notNull(basePackages, "basePackage must not be null");
        this.basePackages = basePackages;
    }

    public static ApplicationContext of(Object... basePackages) {
        return new ApplicationContext(basePackages);
    }

    public void initialize() {
        ConfigurationBeanScanner configurationBeanScanner = ConfigurationBeanScanner.packages(basePackages);
        ClasspathBeanScanner classpathBeanScanner = ClasspathBeanScanner.from(configurationBeanScanner.configurations());

        Collection<BeanConstructor> beanConstructors = new ArrayList<>();
        beanConstructors.addAll(configurationBeanScanner.scan());
        beanConstructors.addAll(classpathBeanScanner.scan());
        beanFactory = BeanFactory.from(beanConstructors);
        beanFactory.initialize();
    }

    public Collection<Object> controllers() {
        return beanFactory.beansAnnotatedWith(Controller.class);
    }
}
