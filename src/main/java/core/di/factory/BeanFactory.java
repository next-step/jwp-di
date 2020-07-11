package core.di.factory;

import core.di.BeanDefinition;
import core.util.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private static final ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

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
            .filter(beanDefinition -> requiredType.isAssignableFrom(beanDefinition.getBeanClass()))
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
        return newClassPathBean(beanDefinition.getConstructor());
    }

    private Object newClassPathBean(Constructor constructor) {
        String[] names = nameDiscoverer.getParameterNames(constructor);
        Parameter[] parameters = constructor.getParameters();
        return ReflectionUtils.newInstance(constructor, addInjectBeans(names, parameters));
    }

    private Object newConfigBean(BeanDefinition beanDefinition) {
        BeanDefinition configClassBean = this.beanDefinitions.stream()
            .filter(b -> b.getBeanClass()
                .isAssignableFrom(beanDefinition.getMethod().getDeclaringClass()))
            .findFirst().get();

        Object bean = this.beans.get(configClassBean);
        if (bean == null) {
            addBean(configClassBean);
            bean = this.beans.get(configClassBean);
        }

        Method method = beanDefinition.getMethod();
        String[] names = nameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();

        try {
            return method.invoke(bean, addInjectBeans(names, parameters));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private BeanDefinition findBeanDefinition(Class clazz, String name) {
        List<BeanDefinition> beanDefinitions = this.beanDefinitions.stream()
            .filter(beanDefinition -> clazz.isAssignableFrom(beanDefinition.getBeanClass()))
            .collect(Collectors.toList());

        if (beanDefinitions.isEmpty()) {
            throw new RuntimeException("not found bean");
        }

        if (beanDefinitions.size() == 1) {
            return beanDefinitions.get(0);
        }

        return beanDefinitions.stream()
            .filter(beanDefinition -> beanDefinition.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("not matched bean"));
    }

    private Object[] addInjectBeans(String[] names, Parameter[] parameters){
        List<Object> parameterBeans = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            BeanDefinition parameterBeanDefinition = findBeanDefinition(parameters[i].getType(), names[i]);
            parameterBeans.add(addBean(parameterBeanDefinition));
        }
        return parameterBeans.toArray();
    }

}
