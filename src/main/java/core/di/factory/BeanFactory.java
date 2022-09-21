package core.di.factory;

import com.google.common.collect.Maps;
import core.di.BeanDefinition;
import core.di.BeanDefinitionRegistry;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private BeanDefinitionRegistry registry;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return this.beans.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        initializeConfigurationBean(this.registry.getMethodBeanDefinitions());
        initializeClassPathBean(this.registry.getClassBeanDefinitions());
    }

    private void initializeClassPathBean(Set<BeanDefinition> classBeanDefinitions) {
        for (BeanDefinition classBeanDefinition : classBeanDefinitions) {
            Class<?> preInstantiateBean = classBeanDefinition.getBeanClass();
            this.beans.put(preInstantiateBean, instantiateClassPathBean(preInstantiateBean));
        }
    }

    private void initializeConfigurationBean(Set<BeanDefinition> methodBeanDefinitions) {
        for (BeanDefinition methodBeanDefinition : methodBeanDefinitions) {
            this.beans.put(methodBeanDefinition.methodReturnType(), instantiateConfigurationBean(methodBeanDefinition.getBeanMethod()));
        }
    }

    private Object instantiateClassPathBean(Class<?> preInstantiateBean) {
        if (this.beans.containsKey(preInstantiateBean)) {
            return this.beans.get(preInstantiateBean);
        }
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiateBean);
        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(preInstantiateBean);
        }
        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateConfigurationBean(Method method) {
        if (this.beans.containsKey(method.getReturnType())) {
            return getBean(method.getReturnType());
        }
        Object configClassInstance = BeanUtils.instantiateClass(method.getDeclaringClass());
        Object[] args = getArguments(method);

        return ReflectionUtils.invokeMethod(method, configClassInstance, args);
    }

    private Object[] getArguments(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int idx = 0; idx < parameterTypes.length; idx++) {
            args[idx] = instantiateMethodParameterBean(parameterTypes[idx]);
        }
        return args;
    }

    private Object instantiateMethodParameterBean(Class<?> parameterType) {
        if (beans.containsKey(parameterType)) {
            return beans.get(parameterType);
        }
        return instantiateConfigurationBean(this.registry.getMethodBeanDefinition(parameterType).getBeanMethod());
    }

    private Object instantiateConstructor(Constructor<?> injectedConstructor) {
        return BeanUtils.instantiateClass(injectedConstructor, getInstantiatedParameters(injectedConstructor));
    }

    private Object[] getInstantiatedParameters(Constructor<?> injectedConstructor) {
        return Arrays.stream(injectedConstructor.getParameterTypes())
                .map(parameterType -> {
                    Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, this.registry.getPreInstantiateClassBean());
                    return instantiateClassPathBean(concreteClass);
                }).toArray();
    }
}
