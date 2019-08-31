package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

public class ApplicationContext {

    protected SimpleBeanFactory beanFactory;

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation) {
        return beanFactory.getBeans(annotation);
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

}
