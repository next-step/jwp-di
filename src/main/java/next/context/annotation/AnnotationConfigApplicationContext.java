package next.context.annotation;

import com.google.common.collect.Maps;
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
import java.util.Map.Entry;
import java.util.stream.Collectors;
import next.config.MyConfiguration;
import org.springframework.util.Assert;

public class AnnotationConfigApplicationContext implements AnnotationConfigRegistry {

    private ClassPathBeanScanner classPathBeanScanner;
    private BeanFactory beanFactory;
    private ConfigurationBeanScanner configurationBeanScanner;
    private List<Object> basePackages = new ArrayList<>();
    private Map<HandlerKey, HandlerExecution> handlerExecutionMap = Maps.newHashMap();

    public AnnotationConfigApplicationContext(Class<?>... configurations) {
        this.beanFactory = new BeanFactory();
        this.classPathBeanScanner = new ClassPathBeanScanner(beanFactory);
        this.configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);

        this.register(configurations);
        this.scan();
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

        if (this.basePackages.isEmpty()) {
            return;
        }

        Map<HandlerKey, HandlerExecution> handlerKeyHandlerExecutionMap = classPathBeanScanner.doScan(this.basePackages.toArray());
        for (Entry<HandlerKey, HandlerExecution> handlerEntry : handlerKeyHandlerExecutionMap.entrySet()) {
            this.handlerExecutionMap.putIfAbsent(handlerEntry.getKey(), handlerEntry.getValue());
        }
    }


    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public Map<HandlerKey, HandlerExecution> getHandlerExecutionMap() {
        return this.handlerExecutionMap;
    }

}
