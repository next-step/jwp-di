package core.di.factory;

import core.annotation.Bean;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created By kjs4395 on 7/19/20
 */
public class ConfigurationBeanScanner2 {

    private final BeanFactory2 beanFactory;

    public ConfigurationBeanScanner2(BeanFactory2 beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> config) {
        beanFactory.register(Arrays.stream(config.getMethods())
                .filter(method -> method.getAnnotation(Bean.class) != null)
                .map(method -> {return
                        new BeanInfo(method.getReturnType(), config, BeanInvokeType.METHOD, null, method);})
                .collect(Collectors.toSet()));
    }
}
