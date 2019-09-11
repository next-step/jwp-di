package core.di.factory;

import com.google.common.collect.Maps;
import core.di.bean.BeanDefinition;
import core.di.bean.DefaultBeanDefinition;
import core.mvc.tobe.AnnotationHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private Map<Class<?>, BeanDefinition> preBeanDefinitions = Maps.newHashMap();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private static final DefaultBeanFactory INSTANCE = new DefaultBeanFactory();

    private DefaultBeanFactory() {
    }

    public static DefaultBeanFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerBeans(Set<BeanDefinition> beanDefinitions) {
        beanDefinitions.stream()
                .forEach(it -> preBeanDefinitions.put(it.getClazz(), it));
    }

    @Override
    public void initialize() {
        preBeanDefinitions.entrySet()
                .stream()
                .forEach(it -> registerBean(it.getKey(), it.getValue()));
        log.debug("Register result bean : {} ", beans);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    @Override
    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotations) {
        return beans.keySet()
                .stream()
                .filter(aClass -> aClass.isAnnotationPresent(annotations))
                .collect(Collectors.toMap(Function.identity(), beans::get));
    }

    private void registerBean(Class<?> clazz, BeanDefinition beanDefinition) {
        Object[] parameters = getParameters(beanDefinition.getParameters());
        beans.putIfAbsent(clazz, beanDefinition.register(parameters));
    }

    private Object[] getParameters(Class<?>[] parameters) {
        if (parameters == null) {
            return null;
        }

        return Arrays.stream(parameters)
                .map(this::getParameter)
                .toArray();
    }

    private Object getParameter(Class<?> parameter) {
        if (beans.containsKey(parameter)) {
            return beans.get(parameter);
        }
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameter, preBeanDefinitions.keySet());
        registerBean(parameter, preBeanDefinitions.get(concreteClass));
        return beans.get(parameter);
    }
}