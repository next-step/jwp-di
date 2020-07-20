package core.di;

import com.google.common.collect.Maps;
import core.di.factory.BeanFactoryUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by iltaek on 2020/07/17 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class BeanDefinitions {

    private final Map<Class<?>, BeanDefinition> beanDefinitions;

    private BeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public static BeanDefinitions fromMap(Map<Class<?>, BeanDefinition> beanDefinitions) {
        return new BeanDefinitions(beanDefinitions);
    }

    public static BeanDefinitions newInstance() {
        return new BeanDefinitions(Maps.newHashMap());
    }

    public static BeanDefinitions from(BeanDefinition beanDefinition) {
        BeanDefinitions beanDefinitions = newInstance();
        beanDefinitions.beanDefinitions.put(beanDefinition.getBeanClass(), beanDefinition);
        return beanDefinitions;
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitionMap() {
        return Collections.unmodifiableMap(beanDefinitions);
    }

    public BeanDefinition getConcreteBeanDefinition(Class<?> typeClass) {
        return get(getConcreteClass(typeClass));
    }

    private BeanDefinition get(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }

    public Class<?> getConcreteClass(Class<?> typeClass) {
        return BeanFactoryUtils.findConcreteClass(typeClass, beanDefinitions.keySet());
    }

    public Set<Class<?>> getPreInstantiateBeans() {
        return beanDefinitions.keySet();
    }

    public void addAll(BeanDefinitions addedBeanDefinitions) {
        this.beanDefinitions.putAll(addedBeanDefinitions.beanDefinitions);
    }

    public BeanDefinition getConfigBeanDefinition(Class<?> configClass) {
        BeanDefinition configBeanDefinition = beanDefinitions.get(configClass);
        if (Objects.isNull(configBeanDefinition)) {
            throw new IllegalStateException(configBeanDefinition + " Config Bean이 존재하지 않는다.");
        }
        return configBeanDefinition;
    }
}
