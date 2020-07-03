/*
package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ConfigurationBeanScanner {
    private static final Class COMPONENT_SCAN_TYPE = ComponentScan.class;
    private static final Class CONFIGURATION_TYPE = Configuration.class;
    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configType) {
        if (Objects.isNull(configType)) {
            return;
        }

        registerConfigurationBeanDefinitions(configType);
        registerBasePackages(configType);
    }

    private void registerBasePackages(Class<?> configType) {
        if (!configType.isAnnotationPresent(COMPONENT_SCAN_TYPE)) {
            return;
        }

        ComponentScan annotation = (ComponentScan) configType.getAnnotation(COMPONENT_SCAN_TYPE);
        beanFactory.registerBasePackages(annotation.basePackages());
    }

    private void registerConfigurationBeanDefinitions(Class<?> configType) {
        if (!configType.isAnnotationPresent(CONFIGURATION_TYPE)) {
             return;
        }

        beanFactory.registerConfigurationTypes(configType);
    }
}
*/
