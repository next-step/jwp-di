package core.mvc.tobe;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScanner.class);

    private BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register() {
        Object[] componentScanPackages = scanPackages();
        if (componentScanPackages == null) {
            logger.warn("NotExist ComponentScan Packages...");
            return;
        }

        Reflections reflections = new Reflections(componentScanPackages);
        reflections.getTypesAnnotatedWith(Configuration.class)
                .forEach(this::register);
    }

    public void register(Class<?> configClass) {
        List<Method> beanMethods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .sorted(Comparator.comparing(Method::getParameterCount))
                .collect(Collectors.toList());

        Object configurationBean = BeanUtils.instantiateClass(configClass);

        for (Method beanMethod : beanMethods) {
            Object[] arguments = arguments(beanMethod);

            try {
                Object bean = beanMethod.invoke(configurationBean, arguments);
                beanFactory.addBean(beanMethod.getReturnType(), bean);
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage());
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private Object[] arguments(Method beanMethod) {
        Parameter[] parameters = beanMethod.getParameters();
        Object[] parameterArgs = new Object[beanMethod.getParameterCount()];

        for (int i = 0; i < parameterArgs.length; i++) {
            parameterArgs[i] = beanFactory.getBean(parameters[i].getType());
        }

        return parameterArgs;
    }

    private Object[] scanPackages() {
        final Reflections reflections = new Reflections("next.config");
        final Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ComponentScan.class);

        for (Class<?> aClass : typesAnnotatedWith) {
            if (aClass.isAnnotationPresent(ComponentScan.class)) {
                return aClass.getAnnotation(ComponentScan.class).value();
            }
        }
        return null;
    }
}
