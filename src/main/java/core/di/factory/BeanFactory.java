package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        for (final Class<?> preInstanticateBean : preInstanticateBeans) {
            try {
                instantiateClass(preInstanticateBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("bean register start");
        for (final Class<?> aClass : beans.keySet()) {
            logger.info("bean register : {}", aClass);
        }
    }


    private Object instantiateClass(Class<?> clazz) throws Exception {
        final Object bean = beans.get(clazz);
        if (Objects.nonNull(bean)) {
            return bean;
        }

        final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        final Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);

        if (Objects.isNull(injectedConstructor)) {
            final Constructor<?> defaultConstructor = concreteClass.getConstructor();
            final Object constructor = defaultConstructor.newInstance();
            beans.put(concreteClass, constructor);
            return constructor;
        }

        final Object constructor = instantiateConstructor(injectedConstructor);
        beans.put(concreteClass, constructor);
        return constructor;
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws Exception {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> args = Lists.newArrayList();
        for (final Class<?> parameterType : parameterTypes) {
            final Object bean = getBean(parameterType);
            if (Objects.nonNull(bean)) {
                args.add(bean);
            } else {
                args.add(instantiateClass(parameterType));
            }

        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

}
