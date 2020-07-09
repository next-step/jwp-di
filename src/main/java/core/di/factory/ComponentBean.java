package core.di.factory;

import java.lang.reflect.Constructor;
import java.util.Map;

public class ComponentBean implements BeanAdapter {
    private Class<?> clazz;

    public ComponentBean(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void addBean(Map<Class<?>, Object> preInstanticateBeanMap, Map<Class<?>, Object> instanceMap) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        preInstanticateBeanMap.put(clazz, constructor);
    }
}
