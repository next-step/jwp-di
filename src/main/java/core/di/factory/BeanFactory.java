package core.di.factory;

import com.google.common.collect.Maps;
import core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private static final BeanFactory beanFactory = new BeanFactory();

    private Set<Class<?>> preInstantiatedBeans; // TODO 상태값을 유지할 필요가 있을까?

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public static BeanFactory getInstance() {
        return beanFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize(Object... basePackage) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanScanner beanScanner = new BeanScanner();
        preInstantiatedBeans = beanScanner.scan(basePackage);
        for (Class<?> preInstantiatedBean : preInstantiatedBeans) {
            Object instantiatedBean = this.getInstantiatedBean(preInstantiatedBean);
            beans.put(preInstantiatedBean, instantiatedBean);
        }
    }

    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    private List<Class<?>> getConstructorParameterClasses(Class<?>[] parameters) {
        List<Class<?>> concreteParameters = new ArrayList<>();
        for (Class<?> parameter : parameters) {
            Class<?> concreteParameter = BeanFactoryUtils.findConcreteClass(parameter, preInstantiatedBeans);
            concreteParameters.add(concreteParameter);
        }

        return concreteParameters;
    }

    private Object getNewInstance(Class<?> preInstantiatedBean, List<Object> parameterBeans) {
        if (parameterBeans.size() == 1) {
            return ReflectionUtils.newInstance(preInstantiatedBean, parameterBeans.get(0));
        }
        if (parameterBeans.size() == 2) {
            return ReflectionUtils.newInstance(preInstantiatedBean, parameterBeans.get(0), parameterBeans.get(1));
        }
        if (parameterBeans.size() == 3) {
            return ReflectionUtils.newInstance(preInstantiatedBean, parameterBeans.get(0), parameterBeans.get(1), parameterBeans.get(2));
        }
        if (parameterBeans.size() == 4) {
            return ReflectionUtils.newInstance(preInstantiatedBean, parameterBeans.get(0), parameterBeans.get(1), parameterBeans.get(2), parameterBeans.get(3));
        }
        if (parameterBeans.size() == 5) {
            return ReflectionUtils.newInstance(preInstantiatedBean, parameterBeans.get(0), parameterBeans.get(1), parameterBeans.get(2), parameterBeans.get(3), parameterBeans.get(4));
        }

        // TODO 더 많은 수의 parameter 필요 경우 케이스
        throw new IllegalArgumentException();
    }

    private Object getInstantiatedBean(Class<?> preInstantiatedBean) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiatedBean);
        if (injectedConstructor == null) {
            return ReflectionUtils.getNoArgsConstructor(preInstantiatedBean).newInstance();
        }

        List<Object> parameterBeans = new ArrayList<>();
        List<Class<?>> concreteParameterClasses = this.getConstructorParameterClasses(injectedConstructor.getParameterTypes());
        for (Class<?> parameterClass : concreteParameterClasses) {
            Object parameterBean = this.beans.get(parameterClass);
            if (parameterBean != null) {
                parameterBeans.add(parameterBean);
                continue;
            }

            parameterBeans.add(this.getInstantiatedBean(parameterClass));
        }

        return this.getNewInstance(preInstantiatedBean, parameterBeans);
    }

}
