package core.di.factory;

import com.google.common.collect.Maps;
import core.di.exception.BeanDuplicationException;
import core.di.exception.CircularDependencyException;
import core.di.factory.generator.BeanGenerators;
import core.di.factory.generator.ConstructorTypeGenerator;
import core.di.factory.generator.MethodTypeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private static final BeanGenerators DEFAULT_BEAN_GENERATOR = new BeanGenerators(
            Arrays.asList(new ConstructorTypeGenerator(), new MethodTypeGenerator())
    );

    private final Set<Class<?>> preInstanticateBeans;
    private final Map<Class<?>, BeanInitInfo> beanInitInfos;
    private final BeanGenerators beanGenerators;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans, BeanGenerators beanGenerators) {
        this.preInstanticateBeans = preInstanticateBeans;
        this.beanInitInfos = preInstanticateBeans.stream()
                .map(BeanInitInfoExtractUtil::extractBeanInitInfo)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.beanGenerators = beanGenerators;
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this(preInstanticateBeans, DEFAULT_BEAN_GENERATOR);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstanticateBeans.forEach(beanType -> createBean(new LinkedHashSet<>(), beanType));
        beans = Collections.unmodifiableMap(beans);
    }

    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(map -> map.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Object createBean(Set<Class<?>> dependency, Class<?> type) {
        if (beans.containsKey(type)) {
            return beans.get(type);
        }

        // change interface to implement class
        type = BeanFactoryUtils.findConcreteClass(type, beanInitInfos);

        checkCircularDependency(dependency, type);

        Object bean = beanGenerators.generate(dependency, this, beanInitInfos.get(type));
        putInContainer(bean, type);
        return bean;
    }

    private void putInContainer(Object instance, Class<?> type) {
        if (beans.containsKey(type)) {
            throw new BeanDuplicationException(type);
        }

        beans.put(type, instance);

        Arrays.stream(type.getInterfaces())
                .forEach(aInterface -> putInContainer(instance, aInterface));
    }

    private void checkCircularDependency(Set<Class<?>> dependency, Class<?> type) {
        if (dependency.contains(type)) {
            List<Class<?>> circularDependency = new ArrayList<>(dependency);
            circularDependency.add(type);

            throw new CircularDependencyException(circularDependency);
        }
    }
}
