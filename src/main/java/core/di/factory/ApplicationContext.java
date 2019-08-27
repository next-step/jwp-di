package core.di.factory;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationContext implements BeanFactory {

    private SimpleBeanFactory simpleBeanFactory;

    private List<String> basePackages = new ArrayList<>();
    private List<BeanDefinition> preBeanDefinitions = new ArrayList<>();

    public ApplicationContext(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            addBasePackages(clazz);
            addPreBeanDefinitions(clazz);
        }
    }

    private void addBasePackages(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            final ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            basePackages.addAll(Arrays.asList(componentScan.basePackages()));
        }
    }

    private void addPreBeanDefinitions(Class<?> clazz) {
        Object target = BeanUtils.instantiateClass(clazz);
        List<BeanDefinition> beanDefinitions = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> new BeanDefinition(target, method))
                .collect(Collectors.toList());
        preBeanDefinitions.addAll(beanDefinitions);
    }

    public void refresh() {
        simpleBeanFactory = new SimpleBeanFactory(basePackages.toArray());
        simpleBeanFactory.registerBeanDefinitions(preBeanDefinitions);
        simpleBeanFactory.initialize();
    }

    @Override
    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation) {
        return simpleBeanFactory.getBeans(annotation);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return simpleBeanFactory.getBean(requiredType);
    }

}
