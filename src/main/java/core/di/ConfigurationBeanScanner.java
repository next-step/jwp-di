package core.di;

import com.google.common.collect.Lists;
import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    public static final String CONFIG_SCAN_PATH = "core.config";
    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register() {
        Reflections reflections = new Reflections(scanPackages());
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
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("빈 추가를 실패 하였습니다. Error Message : " + e);
            }
        }
    }

    private Object[] scanPackages() {
        final Class<? extends Annotation> componentScan = ComponentScan.class;
        Reflections reflections = new Reflections(CONFIG_SCAN_PATH);
        return reflections.getTypesAnnotatedWith(componentScan).toArray();
    }

    private Object[] arguments(Method beanMethod) {
        List<Object> arguments = Lists.newArrayList();
        for (Parameter parameter : beanMethod.getParameters()) {
            Object bean = beanFactory.getBean(parameter.getType());
            if (bean == null) {
                throw new RuntimeException("의존 관계를 주입할 Bean이 없습니다.");
            }
            arguments.add(bean);
        }
        return arguments.toArray();
    }

}
