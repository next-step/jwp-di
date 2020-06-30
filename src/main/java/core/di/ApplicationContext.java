package core.di;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.ComponentScanner;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final Map<Class<?>, Object> beans;

    public ApplicationContext(String appBasePackage) {
        Set<Class<?>> classes = ComponentScanner.scanAfterComponentScan(appBasePackage);
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.createBeanInitInfos(classes);
        BeanFactory beanFactory = new BeanFactory(beanInitInfos);

        this.beans = beanFactory.getInitializedBeans();
    }

    public ApplicationContext(Map<Class<?>, Object> beans) {
        this.beans = beans;
    }

    public ApplicationContext() {
        this("");
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(map -> map.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
