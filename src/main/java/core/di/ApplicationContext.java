package core.di;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactory2;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner2;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created By kjs4395 on 7/20/20
 */
public class ApplicationContext {
    private final Set<Class<?>> configs = new HashSet<>();
    private final BeanFactory2 beanFactory;
    private final ConfigurationBeanScanner2 configurationScanner;
    private ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Class<?> config) {
        configs.add(config);
        this.beanFactory = new BeanFactory2();
        this.configurationScanner = new ConfigurationBeanScanner2(beanFactory);
        this.initialize();
    }

    public ApplicationContext(Set<Class<?>> configs) {
        this.configs.addAll(configs);
        this.beanFactory = new BeanFactory2();
        this.configurationScanner = new ConfigurationBeanScanner2(beanFactory);
        this.initialize();
    }

    private void initialize() {
        initializeConfigBean();
        this.classpathBeanScanner = new ClasspathBeanScanner(this.beanFactory);
        classpathBeanScanner.doScan(readBasePackage());
    }

    private void initializeConfigBean() {
        this.configs.forEach(this.configurationScanner::register);
    }

    private Object[] readBasePackage() {
        return this.configs.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz->clazz.getAnnotation(ComponentScan.class))
                .map(ComponentScan::value)
                .toArray();
    }

    public Map<HandlerKey, HandlerExecution> getHandler() {
        return this.classpathBeanScanner.scan();
    }
}
