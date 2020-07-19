package core.di;

import core.di.factory.BeanInstantiationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class BeanScanners {

    private final BeanScanner beanScanner;
    private final ConfigurationBeanScanner configurationBeanScanner;
    private Set<Class<?>> preInstantiateBeans = new HashSet<>();

    public Set<Class<?>> scan() {
        Set<Class<?>> scannedBeanClasses = beanScanner.scan();
        Set<Class<?>> scannedConfigurationBeanClasses = configurationBeanScanner.scan();

        this.preInstantiateBeans.addAll(scannedBeanClasses);
        for (Class<?> configurationBeanClass : scannedConfigurationBeanClasses) {
            if (this.preInstantiateBeans.contains(configurationBeanClass)) {
                throw new IllegalStateException("preInstantiateBean is duplicate.");
            }

            this.preInstantiateBeans.add(configurationBeanClass);
        }

        return this.preInstantiateBeans;
    }

    public Class<?>[] getParameterTypesForInstantiation(Class<?> preInstantiateBean) {
        preInstantiateBean = BeanInstantiationUtils.findConcreteClass(preInstantiateBean, preInstantiateBeans);
        if (containsOnBeanScanner(preInstantiateBean)) {
            Constructor<?> injectedConstructor = BeanInstantiationUtils.getInjectedConstructor(preInstantiateBean);
            if (injectedConstructor == null) {
                return new Class<?>[0];
            }

            return injectedConstructor.getParameterTypes();
        }

        if (containsOnConfigurationBeanScanner(preInstantiateBean)) {
            Method beanCreationMethod = configurationBeanScanner.getBeanCreationMethod(preInstantiateBean);
            return beanCreationMethod.getParameterTypes();
        }

        return new Class<?>[0];
    }

    public Object instantiate(Class<?> preInstantiateBean, Object... parameterInstances) {
        preInstantiateBean = BeanInstantiationUtils.findConcreteClass(preInstantiateBean, preInstantiateBeans);
        if (containsOnBeanScanner(preInstantiateBean)) {
            Constructor<?> injectedConstructor = BeanInstantiationUtils.getInjectedConstructor(preInstantiateBean);
            if (injectedConstructor == null) {
                return BeanUtils.instantiateClass(preInstantiateBean);
            }

            return BeanUtils.instantiateClass(injectedConstructor, parameterInstances);
        }

        if (containsOnConfigurationBeanScanner(preInstantiateBean)) {
            Method beanCreationMethod = configurationBeanScanner.getBeanCreationMethod(preInstantiateBean);
            return BeanInstantiationUtils.invokeMethod(beanCreationMethod, parameterInstances);
        }

        throw new IllegalStateException("illegal preInstantiateBean Class is instantiated.");
    }

    public Class<?> findConcreteClass(Class<?> preInstantiateBean) {
        if (containsOnBeanScanner(preInstantiateBean)) {
            return beanScanner.findConcreteClass(preInstantiateBean);
        }

        return preInstantiateBean;
    }

    public boolean containsOnBeanScanner(Class<?> preInstantiateBean) {
        return beanScanner.contains(preInstantiateBean);
    }

    public boolean containsOnConfigurationBeanScanner(Class<?> preInstantiateBean) {
        return configurationBeanScanner.contains(preInstantiateBean);
    }

}
