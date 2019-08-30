package core.di.factory.config;

import java.lang.reflect.Method;

public class AnnontatedBeanDefinition extends DefaultBeanDefinition {
    private Method method;
    public AnnontatedBeanDefinition(Class clazz, Method method) {
        super(clazz);
        this.method = method;
    }
}
