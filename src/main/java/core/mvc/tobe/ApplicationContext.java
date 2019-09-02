package core.mvc.tobe;

import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

public class ApplicationContext {
    private BeanFactory beanFactory;

    public ApplicationContext() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> configurations = reflections.getTypesAnnotatedWith(Configuration.class);
        initialize(configurations.toArray(new Class[0]));
    }

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
