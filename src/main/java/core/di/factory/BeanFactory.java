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
        Set<Class<?>> preInstantiatedBeans = beanScanner.scan(basePackage);
        for (Class<?> preInstantiatedBean : preInstantiatedBeans) {
            this.addInstantiatedBean(preInstantiatedBean, preInstantiatedBeans);
        }
    }

    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    private Object getNewInstance(Class<?> preInstantiatedBean, List<Object> parameterBeans) {
        Object[] constructorParameters = new Object[parameterBeans.size()];
        int index = 0;
        for (Object parameterBean : parameterBeans) {
            constructorParameters[index++] = parameterBean;
        }

        return ReflectionUtils.newInstance(preInstantiatedBean, constructorParameters);
    }

    private List<Class<?>> getConstructorParameterClasses(Class<?>[] parameters, Set<Class<?>> preInstantiatedBeans) {
        List<Class<?>> concreteParameters = new ArrayList<>();
        for (Class<?> parameter : parameters) {
            Class<?> concreteParameter = BeanFactoryUtils.findConcreteClass(parameter, preInstantiatedBeans);
            concreteParameters.add(concreteParameter);
        }

        return concreteParameters;
    }

    private Object addInstantiatedBean(Class<?> preInstantiatedBean, Set<Class<?>> preInstantiatedBeans) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object instantiateTargetBean = this.beans.get(preInstantiatedBean);
        if (instantiateTargetBean != null) {
            return instantiateTargetBean;
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiatedBean);
        if (injectedConstructor == null) {
            Object instantiatedBean = ReflectionUtils.getNoArgsConstructor(preInstantiatedBean).newInstance();
            logger.debug("[{}] bean created.", preInstantiatedBean.getName());
            beans.put(preInstantiatedBean, instantiatedBean);
            return instantiatedBean;
        }

        List<Object> parameterBeans = new ArrayList<>();
        List<Class<?>> concreteParameterClasses = this.getConstructorParameterClasses(injectedConstructor.getParameterTypes(), preInstantiatedBeans);
        for (Class<?> parameterClass : concreteParameterClasses) {
            Object parameterBean = this.beans.get(parameterClass);
            if (parameterBean != null) {
                parameterBeans.add(parameterBean);
                continue;
            }

            parameterBeans.add(this.addInstantiatedBean(parameterClass, preInstantiatedBeans));
        }

        Object instantiatedBean = this.getNewInstance(preInstantiatedBean, parameterBeans);
        logger.debug("[{}] bean created.", preInstantiatedBean.getName());
        beans.put(preInstantiatedBean, instantiatedBean);
        return instantiatedBean;
    }

}
