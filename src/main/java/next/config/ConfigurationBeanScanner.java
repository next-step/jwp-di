package next.config;

import java.util.Arrays;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.BeanDefinition;
import core.di.factory.BeanFactory;
import core.mvc.tobe.ClassPathBeanDefinitionScanner;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> componentClass) {
        if (!componentClass.isAnnotationPresent(Configuration.class)) {
            return;
        }

        beanFactory.registerBeanDefinition(componentClass, new BeanDefinition(componentClass));

        Arrays.stream(componentClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Bean.class))
            .forEach(method -> beanFactory.registerBeanDefinition(method.getReturnType(), new BeanDefinition(componentClass, method)));

        if (componentClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = componentClass.getAnnotation(ComponentScan.class);
            Object[] packages = annotation.value();
            System.out.println("packages = " + Arrays.toString(packages));
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
            scanner.scan(packages);
        }
    }
}
