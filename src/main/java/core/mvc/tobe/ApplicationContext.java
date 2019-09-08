package core.mvc.tobe;

import core.di.factory.BeanFactory;

import java.util.Map;

public class ApplicationContext {
    private BeanFactory beanFactory;

    public ApplicationContext(Class<?>... annotatedClasses) {
        initialize(annotatedClasses);
    }

    private void initialize(Class<?>[] annotatedClasses) {
        beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(annotatedClasses);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan(cbs.getBasePackages());
    }

    public Map<Class<?>, Object> getControllers() {
        return beanFactory.getControllers();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }
}
