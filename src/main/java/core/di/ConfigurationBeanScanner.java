package core.di;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.exception.BeanInstantiationException;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * @author : yusik
 * @date : 29/08/2019
 */
public class ConfigurationBeanScanner implements BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScanner.class);

    private final BeanFactory beanFactory;
    private final Set<Class<?>> configClasses = Sets.newHashSet();
    private Set<String> basePackages = Sets.newHashSet();

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void registerConfiguration(Class<?> configClass) {
        if (Objects.nonNull(configClass.getAnnotation(Configuration.class))) {
            configClasses.add(configClass);
        }

        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        if (Objects.nonNull(componentScan)) {
            basePackages.addAll(Sets.newHashSet(componentScan.basePackages()));
        }
    }

    public Set<String> getBasePackages() {
        return basePackages;
    }

    @Override
    public void scan() {
        for (Class<?> configClass : configClasses) {
            Set<Method> instantiateMethods = BeanFactoryUtils.getFactoryMethods(configClass, Bean.class);
            Set<BeanDefinition> beanDefinitions = getBeanDefinitions(getFactoryBean(configClass), instantiateMethods);
            beanFactory.registerBeanDefinition(beanDefinitions);
        }
    }

    private Object getFactoryBean(Class<?> configClass) {
        try {
            return configClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("factoryBean instantiate error: {}", configClass.getTypeName());
            throw new BeanInstantiationException(configClass, e);
        }
    }

    private Set<BeanDefinition> getBeanDefinitions(Object factoryBean, Set<Method> instantiateMethods) {
        Set<BeanDefinition> beanDefinitions = Sets.newHashSet();
        for (Method instantiateMethod : instantiateMethods) {
            BeanDefinition definition = new BeanDefinition(factoryBean, instantiateMethod);
            beanDefinitions.add(definition);
        }
        return beanDefinitions;
    }
}
