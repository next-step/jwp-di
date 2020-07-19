package core.di.factory;

import core.annotation.Bean;
import core.di.BeanDefinitions;
import core.di.ClasspathBeanDefinition;
import core.di.ConfigBeanDefinition;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ConfigurationBeanDefinitionScanner {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ConfigurationBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void register(Class<?>... configClasses) {
        for (Class<?> configClass : configClasses) {
            beanDefinitionRegistry.registerBeanDefinitions(BeanDefinitions.from(new ClasspathBeanDefinition(configClass)));
            Method[] methods = configClass.getMethods();
            Arrays.stream(methods)
                  .filter(method -> method.isAnnotationPresent(Bean.class))
                  .map(method -> BeanDefinitions.from(new ConfigBeanDefinition(method.getReturnType(), method)))
                  .forEach(beanDefinitionRegistry::registerBeanDefinitions);
        }
    }
}
