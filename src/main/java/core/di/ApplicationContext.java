package core.di;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author : yusik
 * @date : 03/09/2019
 */
public class ApplicationContext {

    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?> configurationClass) {
        beanFactory = new BeanFactory();
        initializeScanner(configurationClass);
        beanFactory.initialize();
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    private void initializeScanner(Class<?> configurationClass) {
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.registerConfiguration(configurationClass);

        List<BeanScanner> scanners = Lists.newArrayList();
        scanners.add(configurationBeanScanner);
        scanners.add(new ClasspathBeanScanner(beanFactory, getComponentBasePackages(configurationClass)));
        scanners.forEach(BeanScanner::scan);
    }

    private Set<String> getComponentBasePackages(Class<?> configurationClass) {
        return Optional.ofNullable(configurationClass.getAnnotation(ComponentScan.class))
                .map(ComponentScan::basePackages)
                .map(Sets::newHashSet)
                .orElse(Sets.newHashSet());
    }
}
