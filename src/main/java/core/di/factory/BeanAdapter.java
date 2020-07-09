package core.di.factory;

import java.util.Map;

public interface BeanAdapter {
    void addBean(Map<Class<?>, Object> preInstanticateBeanMap, Map<Class<?>, Object> instanceMap);
}
