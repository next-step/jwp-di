package core.di.factory;

import core.di.BeanDefinition;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<BeanDefinition, Object> beans = new HashMap<>();
    private final List<BeanDefinition> beanDefinitions;

    public BeanFactory(List<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void initialize() {
        for (BeanDefinition beanDefinition : this.beanDefinitions) {
            addBean(beanDefinition);
        }
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanDefinitions.stream()
            .filter(beanDefinition -> beanDefinition.getBeanClass().isAssignableFrom(requiredType))
            .findFirst()
            .map(beanDefinition -> beans.get(beanDefinition))
            .map(o -> requiredType.cast(o))
            .orElse(null);
    }

    public Object[] getBeansByAnnotation(Class<? extends Annotation> annotation) {
        return this.beans.values().stream()
            .filter(o -> o.getClass().isAnnotationPresent(annotation))
            .toArray();
    }

    private Object addBean(BeanDefinition beanDefinition) {
        if (!this.beans.containsKey(beanDefinition)) {
            this.beans.put(beanDefinition, newBean(beanDefinition));
        }
        return this.beans.get(beanDefinition);
    }


    private Object newBean(BeanDefinition beanDefinition) {
        if (beanDefinition.getMethod() != null) {
            return newConfigBean(beanDefinition);
        }
        return newClassPathBean(beanDefinition);
    }

    private Object newClassPathBean(BeanDefinition beanDefinition) {
        Constructor constructor = beanDefinition.getConstructor();

        Object[] parameters = Stream.of(constructor.getParameters())
            .map(parameter -> {
                BeanDefinition parameterBeanDefinition = findBeanDefinition(parameter);
                return addBean(parameterBeanDefinition);
            }).toArray();

        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Object newConfigBean(BeanDefinition beanDefinition) {
        BeanDefinition configClassBean = this.beanDefinitions.stream()
            .filter(b -> b.getBeanClass().isAssignableFrom(beanDefinition.getMethod().getDeclaringClass()))
            .findFirst().get();

        Object bean = this.beans.get(configClassBean);
        if (bean == null) {
            addBean(configClassBean);
            bean = this.beans.get(configClassBean);
        }

        Method method = beanDefinition.getMethod();
        Object[] parameters = Stream.of(method.getParameters())
            .map(parameter -> {
                BeanDefinition parameterBeanDefinition = findBeanDefinition(parameter);
                return addBean(parameterBeanDefinition);
            }).toArray();

        try {
            return method.invoke(bean, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private BeanDefinition findBeanDefinition(Parameter parameter) {
        List<BeanDefinition> beanDefinitions = this.beanDefinitions.stream()
            .filter(beanDefinition -> beanDefinition.getBeanClass()
                .isAssignableFrom(parameter.getType()))
            .collect(Collectors.toList());

        if (beanDefinitions.isEmpty()) {
            throw new RuntimeException("not found bean");
        }

        if (beanDefinitions.size() == 1) {
            return beanDefinitions.get(0);
        }

        return beanDefinitions.stream()
            .filter(beanDefinition -> beanDefinition.getName().equalsIgnoreCase(parameter.getName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("not matched bean"));
    }



}
