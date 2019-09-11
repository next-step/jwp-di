package core.di.tobe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.di.factory.BeanFactoryUtils;
import core.di.tobe.bean.BeanDefinition;
import core.di.tobe.bean.ConfigurationBeanDefinition;
import core.di.tobe.bean.DefaultBeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private Set<BeanDefinition> beanDefinitions = Sets.newHashSet();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private static final DefaultBeanFactory INSTANCE = new DefaultBeanFactory();

    private DefaultBeanFactory() {
    }

    public static DefaultBeanFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerBeans(Set<BeanDefinition> beanDefinitions) {
        log.debug("Register bean : {} ", beanDefinitions);
        this.beanDefinitions.addAll(beanDefinitions);
    }

    @Override
    public void initialize() {
        beanDefinitions.stream().forEach(this::registerBean);
        log.debug("Register result bean : {} ", beans);
    }

    private void registerBean(BeanDefinition beanDefinition) {
        log.debug("Request create bean : {}", beanDefinition);
        if (beanDefinition instanceof DefaultBeanDefinition) {
            initializeBean(beanDefinition.getClazz());
            return;
        }
        initializeBean((ConfigurationBeanDefinition) beanDefinition);
    }

    private void initializeBean(ConfigurationBeanDefinition beanDefinition) {
        if (beans.containsKey(beanDefinition.getClazz())) {
            return;
        }
        Class<?>[] parameters = beanDefinition.getParameters();
        Object[] constructorParameters = getParameters(parameters);
        Object invoke = beanDefinition.newInstance(constructorParameters);

        beans.put(beanDefinition.getClazz(), invoke);
    }

    private void initializeBean(Class clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            beans.computeIfAbsent(clazz, BeanUtils::instantiateClass);
            return;
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        Object[] constructorParameters = getConstructorParameters(parameterTypes);
        log.debug("constructorParameters : {}", constructorParameters);
        beans.put(clazz, BeanUtils.instantiateClass(injectedConstructor, constructorParameters));
    }

    private Object[] getParameters(Class<?>[] parameterTypes) {
        if (parameterTypes == null) {
            return null;
        }

        return Arrays.stream(parameterTypes)
                .map(it -> BeanFactoryUtils.findConcreteClass3(it, beanDefinitions))
                .peek(this::initializeBean)
                .map(this::getBean)
                .toArray();
    }

    private Object[] getConstructorParameters(Class<?>[] parameterTypes) {
        if (parameterTypes == null) {
            return null;
        }
        return Arrays.stream(parameterTypes)
                .map(clazz -> BeanFactoryUtils.findConcreteClass2(clazz, beanDefinitions))
                .peek(this::initializeBean)
                .map(this::getBean)
                .toArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotations) {
        return beans.keySet()
                .stream()
                .filter(aClass -> aClass.isAnnotationPresent(annotations))
                .collect(Collectors.toMap(Function.identity(), beans::get));
    }
}