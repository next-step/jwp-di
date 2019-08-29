package core.di.factory;

import com.google.common.collect.Maps;
import core.di.factory.config.BeanCreator;
import core.di.factory.config.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, BeanDefinition> beanDefinitions;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions.stream()
                .collect(Collectors.toMap(BeanDefinition::getBeanType, Function.identity(), (v1,v2) -> v1));
    }

    public void initialize() {
        for(Class<?> clazz : this.beanDefinitions.keySet()) {
            beans.put(clazz, getInstantiateClass(clazz));
        }
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass){
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object getInstantiateClass(Class<?> clazz) {
        return beans.computeIfAbsent(clazz, this::instantiate);
    }

    private Object instantiate(Class<?> clazz) {
        BeanDefinition beanDefinition = this.beanDefinitions.get(clazz);
        BeanCreator creator = beanDefinition.getBeanCreator();

        List<Object> args = beanDefinition.getArgumentTypes()
                .stream()
                .map(argType -> BeanFactoryUtils.findConcreteClass(argType, this.beanDefinitions.keySet()))
                .map(this::instantiate)
                .collect(Collectors.toList());
        try {
            return creator.create(args.toArray());
        } catch (Exception e) {
            throw new RuntimeException("인스턴스화 중 문제 발생", e);
        }
    }




}
