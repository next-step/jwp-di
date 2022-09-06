package next.context.annotation;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.mvc.tobe.ClassPathBeanScanner;
import core.mvc.tobe.ConfigurationBeanScanner;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import next.context.support.GenericApplicationContext;

public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

    private ClassPathBeanScanner classPathBeanScanner;

    private ConfigurationBeanScanner configurationBeanScanner;
    private List<Object> basePackages = new ArrayList<>();
    private Map<HandlerKey, HandlerExecution> handlerExecutionMap;

    public AnnotationConfigApplicationContext(BeanFactory beanFactory, ClassPathBeanScanner classPathBeanScanner, ConfigurationBeanScanner configurationBeanScanner) {
        super(beanFactory);
        this.classPathBeanScanner = classPathBeanScanner;
        this.configurationBeanScanner = configurationBeanScanner;
    }

    @Override
    public void register(Class<?>... configClasses) {
        for (Class<?> configClass : configClasses) {
            if (!configClass.isAnnotationPresent(Configuration.class)) {
                continue;
            }

            if (configClass.isAnnotationPresent(ComponentScan.class)) {
                ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
                this.basePackages.addAll(
                    Arrays.stream(componentScan.basePackages())
                        .collect(Collectors.toList())
                );
            }

            this.configurationBeanScanner.register(configClass);
        }
    }

    @Override
    public void scan(Object... basePackages) {
        this.basePackages.addAll(
            Arrays.stream(basePackages)
                .collect(Collectors.toList())
        );

        this.handlerExecutionMap = classPathBeanScanner.doScan(this.basePackages.toArray());
    }


    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> getHandlerExecutionMap() {
        return this.handlerExecutionMap;
    }

}
