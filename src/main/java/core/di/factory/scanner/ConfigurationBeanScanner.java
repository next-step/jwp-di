package core.di.factory.scanner;

import core.annotation.Bean;
import core.di.factory.BeanFactory;
import core.di.factory.bean.BeanInfo;
import core.di.factory.bean.BeanInvokeType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created By kjs4395 on 7/19/20
 */
public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
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
