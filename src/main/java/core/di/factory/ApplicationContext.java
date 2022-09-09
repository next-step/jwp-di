package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {
    private Object[] basePackages;
    private BeanFactory beanFactory;

    public ApplicationContext(Object... basePackages) {
        this.basePackages = basePackages;
    }

    public void initialize() {
        BeanScanner beanScanner = new BeanScanner(basePackages);
        Set<Class<?>> preInstantiatedBeans = beanScanner.scan();
        beanFactory = new BeanFactory(preInstantiatedBeans);
        beanFactory.initialize();
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansAnnotatedWith(annotation);
    }
}
