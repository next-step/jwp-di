package core.di;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;
import core.di.scanner.ClassPathBeanScanner;
import core.di.scanner.ConfigurationBeanScanner;

public class ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
    private final BeanFactory beanFactory = new BeanFactory();
    private final List<String[]> basePackages;

    public ApplicationContext(Object... basePackage) {
        this.basePackages = getBasePackageList(basePackage);
    }

    private List<String[]> getBasePackageList(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> componentScanClassSet = reflections.getTypesAnnotatedWith(ComponentScan.class);

        List<String[]> basePackages = new ArrayList<>();
        for (Class<?> componentScanClass : componentScanClassSet) {
            basePackages.add(getBasePackages(componentScanClass));
        }
        return basePackages;
    }

    private String[] getBasePackages(Class<?> configuration) {
        ComponentScan componentScan = configuration.getAnnotation(ComponentScan.class);
        return componentScan.basePackages();
    }

    public void scan() {
        for (String[] basePackage : basePackages) {
            ConfigurationBeanScanner configurationScanner = new ConfigurationBeanScanner(beanFactory);
            ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner(beanFactory);
            Object[] basePackages = Arrays.stream(basePackage).toArray();
            configurationScanner.scan(basePackages);
            classPathBeanScanner.scan(basePackages);
        }
    }

    public void beanInitialize() {
        beanFactory.initialize();
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
