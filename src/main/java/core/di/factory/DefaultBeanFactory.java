package core.di.factory;

import com.google.common.collect.Maps;
import core.di.bean.BeanDefinition;
import core.di.bean.DefaultBeanDefinition;
import core.di.bean.MethodBeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    @Override
    public Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> annotation) {
        return beans.entrySet().stream().filter(it -> it.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void initialize() {
        for (BeanDefinition beanDefinition : beanDefinitions.values()) {
            Object bean = instantiate(beanDefinition);
            beans.put(beanDefinition.getBeanClass(), bean);
        }
    }

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        beanDefinitions.put(beanDefinition.getBeanClass(), beanDefinition);
    }

    private Object instantiate(BeanDefinition beanDefinition) {
        try {
            Class<?>[] beanParameterValues = beanDefinition.getInjectParameterClasses();
            Object[] args = getBeanParameters(beanParameterValues);

            if (beanDefinition instanceof MethodBeanDefinition) {
                MethodBeanDefinition definition = (MethodBeanDefinition) beanDefinition;
                Method method = definition.getBeanMethod();
                return method.invoke(definition.getConfiguration(), args);
            }
            Constructor<?> injectedConstructor = beanDefinition.getInjectedConstructor();
            return BeanUtils.instantiateClass(injectedConstructor, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] getBeanParameters(Class<?>[] constructorParameterClasses) {
        return Arrays.stream(constructorParameterClasses)
                .map(this::createOrGetBean)
                .collect(Collectors.toList())
                .toArray();
    }

    private Object createOrGetBean(Class<?> constructorParameterClass) {
        BeanDefinition beanDefinition = beanDefinitions.get(constructorParameterClass);
        if (Objects.isNull(beanDefinition)) {
            beanDefinition = new DefaultBeanDefinition(constructorParameterClass);
        }
        BeanDefinition concreteBeanDefinition = BeanFactoryUtils.findConcreteClassByBeanDefinition(beanDefinition, beanDefinitions.values());
        Object bean = beans.get(concreteBeanDefinition.getBeanClass());
        if (Objects.isNull(bean)) {
            bean = instantiate(concreteBeanDefinition);
            beans.put(concreteBeanDefinition.getBeanClass(), bean);
        }
        return bean;
    }

}
