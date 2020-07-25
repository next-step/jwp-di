package core.di.factory;

import com.google.common.collect.Maps;
import core.di.factory.exception.CircularReferenceException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BeanFactory {

    private Set<Class<?>> preInstantiateBeans = new HashSet<>();
    private Deque<Class<?>> beanInstantiateHistory = new ArrayDeque<>();
    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            instantiate(preInstantiateBean);
        }
    }

    public void registerPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            if (this.preInstantiateBeans.contains(preInstantiateBean)) {
                throw new IllegalStateException("preInstantiateBean is duplicate.");
            }

            this.preInstantiateBeans.add(preInstantiateBean);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Set<Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return this.beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    private Object instantiate(Class<?> preInstantiateBean) {
        if (this.beanInstantiateHistory.contains(preInstantiateBean)) {
            throw new CircularReferenceException("Illegal Bean Creation Exception : Circular Reference");
        }

        if (beans.containsKey(preInstantiateBean)) {
            return beans.get(preInstantiateBean);
        }

        this.beanInstantiateHistory.push(preInstantiateBean);
        Object instance = instantiateWithInjectedConstructor(preInstantiateBean);
        this.beanInstantiateHistory.pop();

        return instance;
    }

    private Object instantiateWithInjectedConstructor(Class<?> preInstantiateBean) {
        Class<?>[] parameterTypes = getParameterTypesForInstantiation(preInstantiateBean);
        if (parameterTypes.length == 0) {
            return registerBeanWithInstantiating(preInstantiateBean);
        }

        Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteParameterType = findConcreteClass(parameterTypes[i]);

            parameterInstances[i] = instantiate(concreteParameterType);
        }

        return registerBeanWithInstantiating(preInstantiateBean, parameterInstances);
    }

    private Object registerBeanWithInstantiating(Class<?> preInstantiateBean, Object... parameterInstances) {
        Object instance = instantiate(preInstantiateBean, parameterInstances);

        this.beans.put(preInstantiateBean, instance);
        return instance;
    }

    private Class<?>[] getParameterTypesForInstantiation(Class<?> preInstantiateBean) {
        preInstantiateBean = BeanInstantiationUtils.findConcreteClass(preInstantiateBean, preInstantiateBeans);
//        if (containsOnBeanScanner(preInstantiateBean)) {
//            Constructor<?> injectedConstructor = BeanInstantiationUtils.getInjectedConstructor(preInstantiateBean);
//            if (injectedConstructor == null) {
//                return new Class<?>[0];
//            }
//
//            return injectedConstructor.getParameterTypes();
//        }

//        if (containsOnConfigurationBeanScanner(preInstantiateBean)) {
//            Method beanCreationMethod = configurationBeanScanner.getBeanCreationMethod(preInstantiateBean);
//            return beanCreationMethod.getParameterTypes();
//        }

        return new Class<?>[0];
    }

    public Object instantiate(Class<?> preInstantiateBean, Object... parameterInstances) {
        preInstantiateBean = BeanInstantiationUtils.findConcreteClass(preInstantiateBean, preInstantiateBeans);
        // TODO: 2020/07/25 BeanDefinition 에서 필요한 것 : InjectedConstructor, BeanCreationMethod
//        if (containsOnBeanScanner(preInstantiateBean)) {
//            Constructor<?> injectedConstructor = BeanInstantiationUtils.getInjectedConstructor(preInstantiateBean);
//            if (injectedConstructor == null) {
//                return BeanUtils.instantiateClass(preInstantiateBean);
//            }
//
//            return BeanUtils.instantiateClass(injectedConstructor, parameterInstances);
//        }

//        if (containsOnConfigurationBeanScanner(preInstantiateBean)) {
//            Method beanCreationMethod = configurationBeanScanner.getBeanCreationMethod(preInstantiateBean);
//            return BeanInstantiationUtils.invokeMethod(beanCreationMethod, parameterInstances);
//        }

        throw new IllegalStateException("illegal preInstantiateBean Class is instantiated.");
    }

    public Class<?> findConcreteClass(Class<?> preInstantiateBean) {
//        if (containsOnBeanScanner(preInstantiateBean)) {
//            return beanScanner.findConcreteClass(preInstantiateBean);
//        }

        return preInstantiateBean;
    }

}
