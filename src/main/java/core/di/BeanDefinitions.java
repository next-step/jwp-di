package core.di;

import com.google.common.collect.Maps;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.BeanScanner;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by iltaek on 2020/07/17 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class BeanDefinitions {

    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private final BeanScanner beanScanner;

    public BeanDefinitions(BeanScanner beanScanner) {
        this.beanScanner = beanScanner;
    }

    public void addAnnotatedWith(Class<? extends Annotation>... annotations) {
        beanDefinitions.putAll(beanScanner.scanAnnotatedWith(annotations));
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitionMap() {
        return Collections.unmodifiableMap(beanDefinitions);
    }

    public BeanDefinition getConcreteBeanDefinition(Class<?> typeClass) {
        return get(getConcreteClass(typeClass));
    }

    public BeanDefinition get(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }

    public Class<?> getConcreteClass(Class<?> typeClass) {
        return BeanFactoryUtils.findConcreteClass(typeClass, beanDefinitions.keySet());
    }

    public Set<Class<?>> getPreInstantiateBeans() {
        return beanDefinitions.keySet();
    }
}
