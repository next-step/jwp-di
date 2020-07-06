package core.di.factory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanDefinition implements BeanDefinition, InstantiatableBean {
    protected final Class<?> originClass;
    protected final List<Class<?>> dependencies = new ArrayList<>();

    protected AbstractBeanDefinition(Class<?> originClass) {
        this.originClass = originClass;
    }
}
