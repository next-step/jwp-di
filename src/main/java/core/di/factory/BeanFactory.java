package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstanticateBean : this.preInstanticateBeans) {
            this.initialize(preInstanticateBean);
        }
    }

    private Object initialize(Class<?> preInstanticateBean) {
        if (this.beans.containsKey(preInstanticateBean)) {
            return this.beans.get(preInstanticateBean);
        }

        Class<?> concretePreInstanticateBean = BeanFactoryUtils.findConcreteClass(preInstanticateBean, this.preInstanticateBeans);
        Class<?>[] injectedClasses = BeanFactoryUtils.getInjectedClasses(concretePreInstanticateBean);
        Object[] injectedBeans = Arrays.stream(injectedClasses)
                .map(this::initialize)
                .toArray();

        Object bean = this.constructBean(concretePreInstanticateBean, injectedBeans);
        this.beans.put(preInstanticateBean, bean);
        logger.info("Bean({}) has created", bean);
        return bean;
    }

    private Object constructBean(Class<?> preInstanticateBean, Object... beans) {
        try {
            return this.getConstructor(preInstanticateBean).newInstance(beans);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getConstructor(Class<?> preInstanticateBean) throws NoSuchMethodException {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstanticateBean);
        return (injectedConstructor != null) ? injectedConstructor : preInstanticateBean.getDeclaredConstructors()[0];
    }
}
