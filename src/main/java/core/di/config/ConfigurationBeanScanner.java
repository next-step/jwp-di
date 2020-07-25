package core.di.config;

import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigurationBeanScanner {

    public Map<Class<?>, Object> scan() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        BeanFactory beanFactory = new BeanFactory(typesAnnotatedWith);
        beanFactory.initializeByConfig();

        return beanFactory.getConfigurationBeans();
    }
}
