package core.utils;

import core.di.ApplicationContext;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.ComponentScanner;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {
    private Generator() {}

    public static BeanFactory beanFactoryOf(Set<Class<?>> classes) {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.createBeanInitInfos(classes);
        return new BeanFactory(beanInitInfos);
    }

    public static BeanFactory beanFactoryOf(Class<?>... clazz) {
        Set<Class<?>> classes = Arrays.stream(clazz)
                .collect(Collectors.toSet());

        return beanFactoryOf(classes);
    }

    public static BeanFactory beanFactoryOf(String basePackage) {
        Set<Class<?>> classes = ComponentScanner.scan(basePackage);

        return beanFactoryOf(classes);
    }

    public static ApplicationContext appContextOf(Set<Class<?>> classes) {
        return new ApplicationContext(beanFactoryOf(classes).getInitializedBeans());
    }

    public static ApplicationContext appContextOf(Class<?>... clazz) {
        return new ApplicationContext(beanFactoryOf(clazz).getInitializedBeans());
    }

    public static ApplicationContext appContextOf(String basePackage) {
        return new ApplicationContext(beanFactoryOf(basePackage).getInitializedBeans());
    }
}
