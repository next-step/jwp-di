package core.di.context;

import core.annotation.Bean;
import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.BeanDefinition;
import core.di.BeanScanner;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.util.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final BeanScanner beanScanner;
    private final BeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Class<?>... clazz) {
        this.beanScanner = new BeanScanner(findBasePackages(clazz));

        List<BeanDefinition> beanDefinitions = findBasePackageBeanDefinitions();
        beanDefinitions.addAll(findConfigBeanDefinitions());

        this.beanFactory = new BeanFactory(beanDefinitions);
        this.beanFactory.initialize();
    }

    @Override
    public Object[] getBeans(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansByAnnotation(annotation);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    private List<BeanDefinition> findConfigBeanDefinitions() {
        Set<Class<?>> beanClasses = beanScanner.scan(Configuration.class);
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        beanClasses.forEach(aClass -> {
            Method[] methods = aClass.getDeclaredMethods();
            beanDefinitions.addAll(Stream.of(methods).filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> new BeanDefinition() {
                    @Override
                    public String getName() {
                        return method.getName();
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public Constructor getConstructor() {
                        return null;
                    }

                    @Override
                    public Class<?> getBeanClass() {
                        return method.getReturnType();
                    }
                })
            .collect(Collectors.toList()));

        });

        return beanDefinitions;
    }

    private List<BeanDefinition> findBasePackageBeanDefinitions() {
        Set<Class<?>> beanClasses= beanScanner.scan(Component.class);
        return beanClasses.stream().map(aClass -> {
            Component component = AnnotationUtils.findAnnotation(aClass, Component.class);

            String name = StringUtils.isEmpty(component.value())? aClass.getSimpleName() : component.value();
            return new BeanDefinition() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Method getMethod() {
                    return null;
                }

                @Override
                public Constructor getConstructor() {
                    return BeanFactoryUtils.getInjectedConstructor(aClass)
                        .orElseGet(() -> ReflectionUtils.getConstructorByArgs(aClass));
                }

                @Override
                public Class<?> getBeanClass() {
                    return aClass;
                }
            };
        }).collect(Collectors.toList());

    }

    private String[] findBasePackages(Class<?>[] clazzes) {
        List<String> basePackages = new ArrayList<>();
        for (Class<?> annotatedClass : clazzes) {
            ComponentScan componentScan = annotatedClass.getAnnotation(ComponentScan.class);
            if (componentScan == null) {
                continue;
            }

            basePackages.addAll(Arrays.asList(componentScan.basePackages()));
        }
        return basePackages.toArray(new String[basePackages.size()]);
    }


}
