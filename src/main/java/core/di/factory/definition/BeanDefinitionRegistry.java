package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanDefinitionRegistry {

    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    public void addAllPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beanDefinitions.put(preInstantiateBean, new ClassPathBeanDefinition(preInstantiateBean));
        }
    }

    public void addConfigurations(List<Class<?>> configurations) {
        for (Class<?> configuration : configurations) {
            List<Method> beanMethod = getBeanMethod(configuration);
            beanRegister(beanMethod, configuration);
        }
    }

    private void beanRegister(List<Method> beanMethod, Class<?> configuration) {
        for (Method method : beanMethod) {
            beanDefinitions.put(method.getReturnType(), new ConfigurationBeanDefinition(configuration, method));
        }
    }

    private List<Method> getBeanMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toList());
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public BeanDefinition get(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }
}
