package core.di.factory.scanner;

import core.di.factory.BeanFactory;
import core.di.factory.definition.BeanDefinition;
import core.di.factory.definition.BeanDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner implements BeanScanner {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ConfigurationBeanScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }
    @Override
    public void doScan(Class<?>... configurations) {

        beanDefinitionRegistry.addConfigurations(Arrays.stream(configurations)
                .collect(Collectors.toList()));
    }

}
