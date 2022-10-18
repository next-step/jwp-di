package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.bean.Bean;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicationContext {
    private Object[] basePackages;
    private BeanFactory beanFactory;

    public ApplicationContext(Object... basePackages) {
        this.basePackages = basePackages;
    }

    public void initialize() {
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(basePackages);
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(configurationBeanScanner.getConfiguration());

        Collection<Bean> beans = new ArrayList<>();
        beans.addAll(configurationBeanScanner.scan());
        beans.addAll(classpathBeanScanner.scan());
        beanFactory = new BeanFactory(beans);
        beanFactory.initialize();
    }

    public Collection<Object> getControllers() {
        return beanFactory.annotatedWith(Controller.class);
    }
}
