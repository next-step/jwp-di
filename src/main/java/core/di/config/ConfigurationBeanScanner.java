package core.di.config;

import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigurationBeanScanner {

    public static Map<Class<?>, Object> scan(BeanFactory beanFactory) {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        beanFactory.applyConfiguration(typesAnnotatedWith);
        return beanFactory.getConfigurationBeans();
    }
}
