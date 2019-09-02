package core.mvc.tobe;

import java.lang.reflect.Method;

public class BeanDefinition {
    private Object configurationObject;
    private Method beanCreateMethod;

    public BeanDefinition(Object configurationObject, Method beanCreateMethod) {
        this.configurationObject = configurationObject;
        this.beanCreateMethod = beanCreateMethod;
    }

    public Object getConfigurationObject() {
        return this.configurationObject;
    }

    public Method getBeanCreateMethod() {
        return beanCreateMethod;
    }

    public Class<?> getBeanType() {
        return this.beanCreateMethod.getReturnType();
    }

    public Class<?>[] getParameterTypes() {
        return this.beanCreateMethod.getParameterTypes();
    }
}
