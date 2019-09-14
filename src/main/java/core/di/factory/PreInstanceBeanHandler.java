package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class PreInstanceBeanHandler {
    private Set<Class<?>> classPathBeans;

    private Map<Class<?>, Method> configurationBeans;

    public PreInstanceBeanHandler() {
        this.classPathBeans = Sets.newHashSet();
        this.configurationBeans = Maps.newHashMap();
    }

    public void registerPreInstantiateBeans(Set<Class<?>> classPathBeans) {
        this.classPathBeans = classPathBeans;
    }

    public void registerBeanMethods(Map<Class<?>, Method> configurationBeans) {
        this.configurationBeans = configurationBeans;
    }

    public Set<Class<?>> getPreInstanceBeans() {
        Set<Class<?>> preInstanceBeans = Sets.newHashSet();

        preInstanceBeans.addAll(classPathBeans);
        preInstanceBeans.addAll(configurationBeans.keySet());

        return preInstanceBeans;
    }

    public boolean isConfigurationBean(Class<?> clazz) {
        return configurationBeans.containsKey(clazz);
    }

    public Method getMethod(Class<?> clazz) {
        return configurationBeans.get(clazz);
    }

    public Set<Class<?>> getClassPathBeans() {
        return classPathBeans;
    }
}
