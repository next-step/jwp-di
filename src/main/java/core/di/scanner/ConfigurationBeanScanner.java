package core.di.scanner;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner implements Scanner {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScanner.class);

    @Override
    public void scan(BeanFactory beanFactory, String... basePackage) {
        Set<Class<?>> configurationBeans = ReflectionUtils.getPreInstantiatedBeansWithAnnotations(Arrays.asList(Configuration.class), basePackage);
        configurationBeans.forEach(beanFactory::addPreInstantiatedBeans);
        configurationBeans.forEach(beanFactory::addPreInstantiatedBeansFromConfiguration);

        for (Class<?> configurationBean : configurationBeans) {
            List<Method> beans = Arrays.stream(configurationBean.getDeclaredMethods())
                    .filter(this::isBean)
                    .collect(Collectors.toList());
            List<Class<?>> parameterClasses = this.getParameterClasses(beans);
            beanFactory.addAllPreInstantiatedBeansFromConfiguration(parameterClasses);
        }

        logger.debug("ConfigurationBeanScanner scan completed ..... list below");
        beanFactory.getPreInstantiatedBeansFromConfiguration().forEach(preInstantiatedBean -> logger.debug(preInstantiatedBean.getName()));
        logger.debug("----------------------------------------------------");
    }

    public String[] getBasePackages(Class<?> configurationClass) {
        return Arrays.stream(configurationClass.getDeclaredAnnotations())
                .filter(annotation -> annotation.annotationType().equals(ComponentScan.class))
                .findFirst()
                .map(this::getBasePackageFromMethod)
                .orElseThrow(IllegalArgumentException::new);
    }

    private String[] getBasePackageFromMethod(Annotation componentScanAnnotation) {
        Method basePackageMethod = Arrays.stream(componentScanAnnotation.annotationType().getDeclaredMethods())
                .filter(method -> method.getName().equals("basePackages"))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        try {
            return (String[]) basePackageMethod.invoke(componentScanAnnotation);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private List<Class<?>> getParameterClasses(List<Method> beans) {
        List<Class<?>> parameterClasses = new ArrayList<>();
        for (Method bean : beans) {
            List<Class<?>> parameters = Arrays.stream(bean.getParameterTypes())
                    .collect(Collectors.toList());
            parameterClasses.addAll(parameters);
        }

        return parameterClasses;
    }

    private boolean isBean(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation.annotationType().equals(Bean.class));
    }

}
