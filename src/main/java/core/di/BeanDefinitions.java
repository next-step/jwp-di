package core.di;

import core.di.factory.BeanFactoryUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by iltaek on 2020/07/17 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class BeanDefinitions {

    private final Map<Class<?>, BeanDefinition> beanDefinitions;

    public BeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
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
}
