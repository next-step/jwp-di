package core.di.factory;

import core.annotation.Bean;
import core.config.WebMvcConfiguration;

import java.util.Arrays;

public class ConfigurationBeanScanner {
    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<? extends WebMvcConfiguration> clazz) {
        beanFactory.register(clazz);
        beanFactory.addBean(clazz);

        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> beanFactory.register(clazz, method));
    }
}
