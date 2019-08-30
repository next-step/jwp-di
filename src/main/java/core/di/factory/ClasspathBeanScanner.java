package core.di.factory;

import java.util.Set;

public class ClasspathBeanScanner extends BeanScanner<String> {

    public ClasspathBeanScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected void doScan(String... args) {
        final Set<BeanDefinition> beanDefinitions = scanByBasePackages(args);
        registry.registerBeanDefinitions(beanDefinitions);
    }

}
