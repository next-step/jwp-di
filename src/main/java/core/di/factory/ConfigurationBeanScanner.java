package core.di;

import core.di.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?>... configurations) {
        beanFactory.addConfigurations(Arrays.stream(configurations)
                .collect(Collectors.toList()));
    }
}
