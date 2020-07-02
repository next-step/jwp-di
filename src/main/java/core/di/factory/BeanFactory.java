package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<>();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {

    }

    @Override
    public void registerDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.put(beanDefinition.getName(), beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> type) {
        return this.beanDefinitions.get(type.getName());
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }
}
