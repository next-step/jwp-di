package core.di.factory;

import com.google.common.collect.Maps;
import core.di.exception.BeanCreateException;
import core.di.exception.BeanDuplicationException;
import core.di.exception.CircularDependencyException;
import core.di.factory.generator.BeanGenerators;
import core.di.factory.generator.ConstructorTypeGenerator;
import core.di.factory.generator.MethodTypeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private static final BeanGenerators DEFAULT_BEAN_GENERATOR = new BeanGenerators(
            Arrays.asList(new ConstructorTypeGenerator(), new MethodTypeGenerator())
    );

    private final Map<Class<?>, BeanInitInfo> beanInitInfos;
    private final BeanGenerators beanGenerators;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Map<Class<?>, BeanInitInfo> beanInitInfos, BeanGenerators beanGenerators) {
        this.beanInitInfos = beanInitInfos;
        this.beanGenerators = beanGenerators;

        initialize();
    }

    public BeanFactory(Map<Class<?>, BeanInitInfo> beanInitInfos) {
        this(beanInitInfos, DEFAULT_BEAN_GENERATOR);
    }

    private void initialize() {
        beanInitInfos.keySet()
                .forEach(beanType -> createBean(new LinkedHashSet<>(), beanType));
        beans = Collections.unmodifiableMap(beans);
    }

    public Object createBean(Set<Class<?>> dependency, Class<?> type) {
        if (beans.containsKey(type)) {
            return beans.get(type);
        }

        // change interface to implement class
        type = BeanFactoryUtils.findConcreteClass(type, beanInitInfos);

        checkCircularDependency(dependency, type);

        Object bean = beanGenerators.generate(dependency, this, getBeanInitInfo(type));
        putInContainer(bean, type);
        return bean;
    }

    private BeanInitInfo getBeanInitInfo(Class<?> type) {
        if (!beanInitInfos.containsKey(type)) {
            throw new BeanCreateException("No such type bean init info : " + type);
        }

        return beanInitInfos.get(type);
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

    public Map<Class<?>, Object> getInitializedBeans() {
        return Collections.unmodifiableMap(beans);
    }
}
