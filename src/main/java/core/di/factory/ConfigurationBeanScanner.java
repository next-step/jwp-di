package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner extends BeanScanner<Class<?>> {

    public ConfigurationBeanScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected void doScan(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Configuration.class)) {
                continue;
            }

            scanByClass(clazz);
        }
    }

    private void scanByClass(Class<?> clazz) {
        Object target = BeanUtils.instantiateClass(clazz);
        Set<BeanDefinition> beanDefinitions = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> new BeanDefinition(target, method))
                .collect(Collectors.toSet());
        registry.registerBeanDefinitions(beanDefinitions);
    }

}
